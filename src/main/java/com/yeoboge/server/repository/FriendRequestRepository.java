package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.entity.FriendRequest;
import com.yeoboge.server.enums.error.FriendRequestErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link FriendRequest} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long>, CustomFriendRequestRepository {
    /**
     * 친구 요청 목록을 페이징을 적용해 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 페이징된 친구 요청한 사용자의 {@link FriendInfoDto} 목록
     */
    Page<FriendInfoDto> getFriendRequestsPage(final Long id, final Pageable pageable);

    /**
     * 친구 요청 수신자와 송신자의 아이디로 친구 요청이 이미 존재하는지 확인함
     *
     * @param requesterId 송신자 id
     * @param receiverId 수신자 id
     * @return 해당 친구 요청이 존재하면 true, 아니면 false
     */
    boolean existsByReceiverIdAndRequesterId(Long receiverId, Long requesterId);
    Optional<FriendRequest> findByReceiverIdAndRequesterId(Long receiverId, Long requesterId);
    default FriendRequest getByReceiverIdAndRequesterId(Long receiverId, Long requesterId) {
        return findByReceiverIdAndRequesterId(receiverId, requesterId)
                .orElseThrow(() -> new AppException(FriendRequestErrorCode.REQUEST_INVALID));
    }

}
