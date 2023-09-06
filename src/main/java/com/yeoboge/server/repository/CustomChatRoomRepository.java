package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomChatRoomRepository {
    Page<ChatRoomResponse> getMyChatRoomList(Pageable pageable, User currentUser);
}
