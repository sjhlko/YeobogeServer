package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.vo.chat.StompMessageVo;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.service.StompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StompServiceImpl implements StompService {
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void sendMessage(SimpMessageHeaderAccessor accessor, Long targetUserId, Map<String,Object> request) {
        List<String> idList = accessor.getNativeHeader("id");
        if (idList == null) throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
        Long id = Long.parseLong(idList.get(0));
        StompMessageVo response = StompMessageVo.builder()
                .sender(id)
                .msg((String) request.get("msg"))
                .timeStamp((String) request.get("timeStamp"))
                .build();
        messagingTemplate.convertAndSend("/sub/send-message/" + targetUserId, response);
        messagingTemplate.convertAndSend("/sub/send-message/" + id, response);
    }
}
