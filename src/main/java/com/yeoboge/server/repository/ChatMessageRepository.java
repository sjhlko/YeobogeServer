package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link ChatMessage} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 채팅방의 모든 채팅 내역을 조회하여 페이징된  {@link ChatMessage} 엔티티를 반환함.
     *
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @param chatRoom 채팅 내역을 조회하고자 하는 채팅방 entity {@link ChatRoom}
     * @return 페이징 된 해당 채팅방의 모든 채팅
     */
    Page<ChatMessage> findAllByChatRoom(Pageable pageable, ChatRoom chatRoom);

    /**
     * 특정 채팅방의 특정 유저가 보내지 않은 메세지 중 특정 읽음 상태인 메세지 엔티티 리스트를 반환함 {@link ChatMessage}
     *
     * @param chatRoom 채팅 내역을 조회하고자 하는 채팅방 {@link ChatRoom}
     * @param user 해당 유저가 보내지 않은 메세지만을 필터링 하여 리턴함 {@link User}
     * @param isRead 해당 읽음 상태인 메세지만을 조회함 {@link IsRead}
     * @return 페이징 된 해당 채팅방의 해당 조건을 만족한 리스트
     */
    List<ChatMessage> findAllByChatRoomAndUserIsNotAndIsReadIs(ChatRoom chatRoom, User user, IsRead isRead);
}
