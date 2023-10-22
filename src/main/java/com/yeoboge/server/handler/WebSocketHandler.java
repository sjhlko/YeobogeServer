package com.yeoboge.server.handler;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.ChatMessageService;
import com.yeoboge.server.service.ChatRoomService;
import com.yeoboge.server.service.PushAlarmService;
import com.yeoboge.server.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 웹 소켓관련 세션 연결, 종료, 메세지 발송 등에 관련한 Handler
 */
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final List<HashMap<String, Object>> sessionList = new ArrayList<>(); //웹소켓 세션을 담아둘 리스트 ---roomListSessions
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PushAlarmService pushAlarmService;
    private final SSEService sseService;


    /**
     * 특정 세션에 메세지 발송, 저장 등을 처리하는 메소드
     * <p>
     * 특정 세션애 메세지가 발송 될 때 해당 세션과 같은 방에 연결돼있는 세션에 해당 메세지를 전부 전송한다.
     * 열린 세션의 갯수를 세어 읽음 정보를 파악한 뒤 해당 메세지를 db에 저장한다.
     * 열린 세션의 갯수를 통해 상대방의 접속 정보를 파악한 뒤 상대방에게 푸시 알림을 전송한다.
     *
     * @param session 메세지를 발송 할 세션 id
     * @param message 발송될 메세지
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        //메시지 발송
        String payload = message.getPayload();
        JSONObject obj = jsonToObjectParser(payload);

        String msg = (String) obj.get("msg");
        String timeStamp = (String) obj.get("timeStamp");
        String url = session.getUri().toString();
        String targetUserId = url.split("/chats/send-message/")[1];

        String token = session.getHandshakeHeaders().get("Authorization").get(0).split("Bearer")[1];
        Long currentUserId = jwtProvider.parseUserId(token);
        User currentUser = userRepository.getById(currentUserId);

        String roomId = String.valueOf(chatRoomService.findChatRoomIdByUsers(
                currentUserId,
                Long.parseLong(targetUserId)));
        obj.put("sender", currentUserId);
        //여기서 룸 아이디 유효성 검증 또는 사람 아이디로 받고 검증
        HashMap<String, Object> temp = new HashMap<>();
        int openedSessionCount = 0;
        if (sessionList.size() > 0) {
            for (int i = 0; i < sessionList.size(); i++) {
                String roomNumber = (String) sessionList.get(i).get("roomId"); //세션리스트의 저장된 방번호를 가져와서
                if (roomNumber.equals(roomId)) { //같은값의 방이 존재한다면
                    temp = sessionList.get(i); //해당 방번호의 세션리스트의 존재하는 모든 object값을 가져온다.
                    break;
                }
            }
            //해당 방의 세션들만 찾아서 메시지를 발송해준다.
            for (String k : temp.keySet()) {
                if (k.equals("roomId")) { //다만 방번호일 경우에는 건너뛴다.
                    continue;
                }

                WebSocketSession wss = (WebSocketSession) temp.get(k);

                if (wss != null) {
                    try {
                        openedSessionCount++;
                        wss.sendMessage(new TextMessage(obj.toJSONString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (openedSessionCount >= 2)
            chatMessageService.saveMessage(msg, timeStamp, Long.valueOf(roomId), currentUserId, IsRead.YES);
        else {
            chatMessageService.saveMessage(msg, timeStamp, Long.valueOf(roomId), currentUserId, IsRead.NO);
            pushAlarmService.sendPushAlarm(currentUserId, Long.valueOf(targetUserId), msg,
                    PushAlarmType.CHATTING, 0);
            sseService.send(targetUserId, "chatting", "chatting",
                    ChatRoomResponse.of(Long.parseLong(roomId), msg, timeStamp, currentUser));
        }
    }

    /**
     * 웹소켓 연결 시 처리될 동작을 관리하는 메소드
     * <p>
     * 세션이 어느 채팅방에 연결되었는지를 확인한 뒤, 안읽은 메세지에 대한 읽음 처리를 진행한다.
     * 해당 채팅방에 최초로 연결된 세션의 경우 해당 채팅방 id를 sessionList 에 추가한 뒤 세션 id와 세션의 정보를 추가한다.
     *
     * @param session 웹소켓에 연결할 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //소켓 연결
        super.afterConnectionEstablished(session);
        boolean flag = false;
        String url = session.getUri().toString();
        String token = session.getHandshakeHeaders().get("Authorization").get(0).split("Bearer")[1];
        Long currentUserId = jwtProvider.parseUserId(token);
        String targetUserId = url.split("/chats/send-message/")[1];
        String roomNumber = String.valueOf(chatRoomService.findChatRoomIdByUsers(
                currentUserId,
                Long.parseLong(targetUserId)));
        //방 접속시 읽음 상태를 변경
        chatMessageService.changeReadStatus(Long.parseLong(roomNumber), currentUserId);
        int idx = sessionList.size(); //방의 사이즈를 조사한다.
        if (sessionList.size() > 0) {
            for (int i = 0; i < sessionList.size(); i++) {
                String roomId = (String) sessionList.get(i).get("roomId");
                if (roomId.equals(roomNumber)) {
                    flag = true;
                    idx = i;
                    break;
                }
            }
        }

        if (flag) { //존재하는 방이라면 세션만 추가한다.
            HashMap<String, Object> map = sessionList.get(idx);
            map.put(session.getId(), session);
        } else { //최초 생성하는 방이라면 방번호와 세션을 추가한다.
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("roomId", roomNumber);
            map.put(session.getId(), session);
            sessionList.add(map);
        }

        //세션등록이 끝나면 발급받은 세션ID값의 메시지를 발송한다.
        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());

        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    /**
     * 웹소켓 연결 종료 시 처리될 동작을 관리하는 메소드
     * <p>
     * 웹소켓 연결을 종료 할 세션의 id를 sessionList에서 삭제한다.
     *
     * @param session 웹소켓 연결을 종료 할 세션
     * @param status  종료 상태에 대한 정보를 담은 {@link CloseStatus}
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //소켓 종료
        if (sessionList.size() > 0) { //소켓이 종료되면 해당 세션값들을 찾아서 지운다.
            for (int i = 0; i < sessionList.size(); i++) {
                sessionList.get(i).remove(session.getId());
            }
        }
        super.afterConnectionClosed(session, status);
    }

    /**
     * json 형식의 문자열을 JSONObject로 변환하여 리턴함.
     *
     * @param jsonStr Json 객체로 변환 할 문자열
     * @return 변환 된 {@link JSONObject}
     */
    private static JSONObject jsonToObjectParser(String jsonStr) {
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
