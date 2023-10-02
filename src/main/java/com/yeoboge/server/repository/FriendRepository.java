package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * {@link Friend} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
public interface FriendRepository extends JpaRepository<Friend, Long>, CustomFriendRepository {
    boolean existsByFollowerIdAndOwnerId(Long followerId, Long ownerId);

    /**
     * 사용자 ID 리스트에서 특정 사용자와 친구인 사용자들의 ID만 조회함.
     *
     * @param userId 친구를 조회할 사용자 ID
     * @param idList 사용자 ID 리스트
     * @return 해당 사용자와 친구인 사용자들의 ID 리스트
     */
    @Query("SELECT f.follower.id FROM Friend f WHERE f.owner.id = :userId AND f.follower.id IN :idList")
    List<Long> findFriendIdsInIdList(long userId, List<Long> idList);
}