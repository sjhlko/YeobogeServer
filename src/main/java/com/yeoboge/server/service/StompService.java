package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.chat.StompMessageRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Map;

public interface StompService {

    void sendMessage(SimpMessageHeaderAccessor accessor, Long targetUserId, StompMessageRequest request);
}
