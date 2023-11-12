package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.chat.StompMessageRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface StompService {

    void sendMessage(SimpMessageHeaderAccessor accessor, Long id, StompMessageRequest request);
}

