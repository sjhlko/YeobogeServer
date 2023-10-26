package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.ChattingErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link ChatRoom} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {
    /**
     * 두 유저간에 생성된 채팅방 {@link ChatRoom} 엔티티를 반환함.
     *
     * @param currentUser 현재 로그인한 회원
     * @param targetUser 채팅 상대방인 회원
     * @return {@link ChatRoom} 두 회원 간에 생성된 채팅방 entity
     */
    Optional<ChatRoom> findByCurrentUserAndTargetUser(User currentUser, User targetUser);

    /**
     * 특정 채팅방 id를 통해 {@link ChatRoom} 엔티티를 반환함.
     *
     * @param chatRoomId 조회하고자 하는 채팅방 id
     * @return {@link ChatRoom} 해당 채팅방 id에 대한 채팅방 entity
     * @throws AppException 해당 채팅방이 존재하지 않을 때, 404 응답인 {@code NOT_FOUND} 예외를 던짐.
     */
    default ChatRoom getById(Long chatRoomId) {
        return findById(chatRoomId).orElseThrow(() -> new AppException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));
    }
}
