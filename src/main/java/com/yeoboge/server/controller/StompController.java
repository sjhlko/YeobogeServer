package com.yeoboge.server.controller;

import com.yeoboge.server.domain.vo.chat.StompMessageRequest;
import com.yeoboge.server.service.StompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StompController {
    private final StompService stompService;

    @CrossOrigin
    @MessageMapping("/send-message/{id}")
    public void test(StompMessageRequest request, @DestinationVariable Long id, SimpMessageHeaderAccessor accessor) {
        stompService.sendMessage(accessor, id, request);
    }
}
