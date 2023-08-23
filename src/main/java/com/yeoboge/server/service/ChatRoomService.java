package com.yeoboge.server.service;

public interface ChatRoomService {
    Long findChatRoomIdByUsers(Long currentUserId, Long targetUserId);
}
