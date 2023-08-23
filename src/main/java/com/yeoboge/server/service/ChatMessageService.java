package com.yeoboge.server.service;

public interface ChatMessageService {
    void saveMessage(String message, Long ChatRoomId, Long userId);
}
