package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.ChattingErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {
    Optional<ChatRoom> findByCurrentUserAndTargetUser(User currentUser, User targetUser);
    default ChatRoom getById(Long chatRoomId) {
        return findById(chatRoomId).orElseThrow(() -> new AppException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));
    }
}
