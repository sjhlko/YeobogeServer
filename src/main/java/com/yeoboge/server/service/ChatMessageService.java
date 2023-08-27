package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {
    void saveMessage(String message, String timeStamp, Long ChatRoomId, Long userId);

    PageResponse getChatMessages(Long currentUserId, Long id, Pageable pageable);
}
