package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findAllByChatRoom(Pageable pageable, ChatRoom chatRoom);
}
