package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.vo.chat.StompMessageRequest;
import com.yeoboge.server.domain.vo.chat.StompMessageResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.repository.ChatRoomRepository;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StompServiceImpl implements StompService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final PushAlarmService pushAlarmService;
    private final ChatRoomRepository chatRoomRepository;
//    private static Map<String, Long> openedSession = new HashMap<>();

    @Override
    public void sendMessage(SimpMessageHeaderAccessor accessor, Long id, StompMessageRequest request) {
        List<String> idList = accessor.getNativeHeader("id");
        if (idList == null) throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
        Long userId = Long.parseLong(idList.get(0));
        ChatRoom chatRoom = chatRoomRepository.getById(id);
        Long targetUserId =
                Objects.equals(chatRoom.getTargetUser().getId(), userId) ?
                        chatRoom.getCurrentUser().getId() : chatRoom.getTargetUser().getId();
        StompMessageResponse response = StompMessageResponse.builder()
                .sender(userId)
                .msg(request.msg())
                .timeStamp(request.timeStamp())
                .build();
        IsRead isRead = accessor.getNativeHeader("opponentConnected").get(0).equals("true")
                ? IsRead.YES : IsRead.NO;
        if (isRead.equals(IsRead.NO))
            pushAlarmService.sendPushAlarm(userId, targetUserId, request.msg(), PushAlarmType.CHATTING, 0);
        chatMessageService.saveMessage(request.msg(), request.timeStamp(), id, userId, isRead);
        messagingTemplate.convertAndSend("/sub/send-message/" + id, response);
    }

}
