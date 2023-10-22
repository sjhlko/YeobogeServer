package com.yeoboge.server.repository.impl;

import com.yeoboge.server.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCaches = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String id, SseEmitter sseEmitter) {
        emitters.put(id, sseEmitter);
        return emitters.get(id);
    }

    @Override
    public void saveEventCache(String id, Object event) {
        eventCaches.put(id, event);
    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmittersByIdStartsWith(String id) {
        for (String key : emitters.keySet()) {
            if (key.startsWith(id + "_")) emitters.remove(key);
        }
    }

    @Override
    public void deleteAllEventCachesByIdStartsWith(String id) {
        for (String key : eventCaches.keySet()) {
            if (key.startsWith(id + "_")) eventCaches.remove(key);
        }
    }

    @Override
    public Map<String, SseEmitter> findAllEmittersByIdStartsWith(String id) {
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().startsWith(id + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCachesByIdStartsWith(String id) {
        return eventCaches.entrySet().stream()
                .filter(e -> e.getKey().startsWith(id + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
