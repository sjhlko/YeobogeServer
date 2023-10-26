package com.yeoboge.server.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(String id, SseEmitter sseEmitter);

    void saveEventCache(String id, Object event);
    void deleteById(String id);

    void deleteAllEmittersByIdStartsWith(String id);

    void deleteAllEventCachesByIdStartsWith(String id);

    Map<String, SseEmitter> findAllEmittersByIdStartsWith(String id);

    Map<String, Object> findAllEventCachesByIdStartsWith(String id);
}
