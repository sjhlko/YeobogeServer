package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.user.RequestFriendRequest;
import org.springframework.data.domain.Pageable;

/**
 * 친구 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface FriendService {
    /**
     * 회원의 친구 목록을 페이징하여 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 관련 정보가 포함된 {@link Pageable}
     * @return 친구 DTO 리스트를 포함한 {@link PageResponse}
     */
    PageResponse getFriends(Long id, Pageable pageable);

    /**
     * 회원의 친구 요청 목록을 페이징하여 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 관련 정보가 포함된 {@link Pageable}
     * @return 요청을 보낸 사용자의 DTO 리스트를 포함한 {@link PageResponse}
     */
    PageResponse getFriendRequests(Long id, Pageable pageable);

    /**
     * 친구 요청을 보내기 위해 특정 사용자의 닉네임으로 사용자를 검색함
     *
     * @param nickname 친구요청을 보내고픈 사용자의 닉네임
     * @return 해당 닉네임을 가진 사용자의 정보를 담은 {@link FriendInfoDto} DTO
     */
    FriendInfoDto searchUserByNickname(String nickname);

    /**
     * 특정 사용자에게 친구 요청 보내기
     *
     * @param id 현재 로그인한 회원 ID
     * @param request 친구 요청을 보내고픈 유저의 id를 담은 {@link RequestFriendRequest} VO
     * @return 친구 요청 성공 메세지를 담은 {@link MessageResponse}
     */
    MessageResponse requestFriend(Long id, RequestFriendRequest request);

    /**
     * 특정 사용자의 친구 요청 수락하기
     *
     * @param currentUserId 현재 로그인한 회원 ID
     * @param id 친구 요청을 수락하고픈 유저의 id
     * @return 친구 요청 수락 성공 메세지를 담은 {@link MessageResponse}
     */
    MessageResponse acceptFriendRequest(Long currentUserId, Long id);

    /**
     * 특정 사용자의 친구 요청 거절하기
     *
     * @param currentUserId 현재 로그인한 회원 ID
     * @param id 친구 요청을 거절하고픈 유저의 id
     * @return 친구 요청 거절 성공 메세지를 담은 {@link MessageResponse}
     */
    MessageResponse denyFiendRequest(Long currentUserId, Long id);
}
