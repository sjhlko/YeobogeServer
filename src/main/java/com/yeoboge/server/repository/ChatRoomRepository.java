package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByCurrentUserAndTargetUser(User currentUser, User targetUser);
    Page<ChatRoom> findAllByCurrentUserOrTargetUser(Pageable pageable, User currentUser, User targetUser);
}
