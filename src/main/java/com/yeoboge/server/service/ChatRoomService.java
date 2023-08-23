package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    Long findChatRoomIdByUsers(Long currentUserId, Long targetUserId);
    PageResponse getChatRooms (Long id, Pageable pageable);
}
