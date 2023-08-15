package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link Friend} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
public interface FriendRepository extends JpaRepository<Friend, Long>, CustomFriendRepository {
    boolean existsByFollowerIdAndOwnerId(Long followerId, Long ownerId);
}