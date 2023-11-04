package com.yeoboge.server.controller;

import com.yeoboge.server.domain.vo.chat.StompMessageVo;
import com.yeoboge.server.service.StompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StompController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final StompService stompService;

    @CrossOrigin
    @MessageMapping("/send-message/{targetUserId}")
    public void test(Map<String,Object> request, @DestinationVariable Long targetUserId, SimpMessageHeaderAccessor accessor) {
        stompService.sendMessage(accessor, targetUserId, request);
    }
}
