package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.vo.chat.StompMessageRequest;
import com.yeoboge.server.domain.vo.chat.StompMessageResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.service.ChatMessageService;
import com.yeoboge.server.service.PushAlarmService;
import com.yeoboge.server.service.StompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StompServiceImpl implements StompService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final PushAlarmService pushAlarmService;
    private static Map<String, Long> openedSession = new HashMap<>();

    @Override
    public void sendMessage(SimpMessageHeaderAccessor accessor, Long targetUserId, StompMessageRequest request) {
        List<String> idList = accessor.getNativeHeader("id");
        if (idList == null) throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
        Long id = Long.parseLong(idList.get(0));
        Long roomId = Long.parseLong(accessor.getNativeHeader("roomId").get(0));
        StompMessageResponse response = StompMessageResponse.builder()
                .sender(id)
                .msg(request.msg())
                .timeStamp(request.timeStamp())
                .build();
        IsRead isRead = accessor.getNativeHeader("opponentConnected").get(0).equals("true")
                ? IsRead.YES : IsRead.NO;
        if(isRead.equals(IsRead.NO))
            pushAlarmService.sendPushAlarm(id,targetUserId, request.msg(), PushAlarmType.CHATTING, 0);
        chatMessageService.saveMessage(request.msg(), request.timeStamp(), roomId, id, isRead);
        messagingTemplate.convertAndSend("/sub/send-message/" + roomId, response);
    }

}
