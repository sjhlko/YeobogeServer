package com.yeoboge.server.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSEService {
    SseEmitter subscribe(Long id, String lastEventId);

    void send(String id, String content, String type, Object data);
}
