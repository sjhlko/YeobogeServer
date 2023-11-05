package com.yeoboge.server.service.impl;

import com.yeoboge.server.repository.EmitterRepository;
import com.yeoboge.server.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SSEServiceImpl implements SSEService {
    private final EmitterRepository emitterRepository;
    private static final long TIMEOUT = Long.MAX_VALUE;

    @Override
    public SseEmitter subscribe(Long id, String lastEventId) {
        String emitterId = makeEmitterId(id);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteAllEmittersByIdStartsWith(id.toString()));
        emitter.onTimeout(() -> emitterRepository.deleteAllEmittersByIdStartsWith(id.toString()));
        emitter.onError((e) -> emitterRepository.deleteAllEmittersByIdStartsWith(id.toString()));
        sendToClient(emitter, "0","initMessage","initMessage");
        System.out.println("보냄");
        return emitter;

    }

    @Override
    public void send(String id, String content, String type, Object data) {
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmittersByIdStartsWith(id);
        for (String key : emitters.keySet()) {
            emitterRepository.saveEventCache(key, data);
            sendToClient(emitters.get(key), key, type, data);
        }
    }

    private void sendToClient(SseEmitter emitter, String id, String name, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name(name)
                    .data(data));
//                    .reconnectTime(0));
//            emitter.complete();
//            emitterRepository.deleteById(id);
        }
        catch (Exception e) {
            emitterRepository.deleteById(id);
            emitter.completeWithError(e);
        }
    }

    private String makeEmitterId(Long id) {
        return id.toString() + "_" + System.currentTimeMillis();
    }
}
