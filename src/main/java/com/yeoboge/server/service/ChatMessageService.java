package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.entity.IsRead;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {
    void saveMessage(String message, String timeStamp, Long ChatRoomId, Long userId, IsRead isRead);

    PageResponse getChatMessages(Long currentUserId, Long id, Pageable pageable);
}
