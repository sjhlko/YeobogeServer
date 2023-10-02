package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.domain.vo.user.RequestFriendRequest;
import com.yeoboge.server.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 친구 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 */
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * 회원의 친구 목록을 조회하는 API
     * @param id 현재 로그인한 회원 ID
     * @param pageable 페이징 관련 파라미터를 받기 위한 인터페이스 {@link Pageable}
     * @return 페이징된 친구 리스트와 페이지 정보를 담은 {@link PageResponse}의 HTTP 200 응답
     */
    @GetMapping()
    public Response<PageResponse> getFriends(@AuthenticationPrincipal Long id, Pageable pageable) {
        PageResponse response = friendService.getFriends(id, pageable);
        return Response.success(response);
    }

    /**
     * 회원에게 요청된 친구 요청 목록을 조회하는 API
     *
     * @param id 현재 로그인한 회원 ID
     * @param pageable 페이징 관련 파라미터를 받기 위한 인터페이스 {@link Pageable}
     * @return 페이징된 친구 요청 리스트와 페이지 정보를 담은 {@link PageResponse}의 HTTP 200 응답
     */
    @GetMapping("/requests")
    public Response<PageResponse> getFriendRequests(
            @AuthenticationPrincipal Long id,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        PageResponse response = friendService.getFriendRequests(id, pageable);
        return Response.success(response);
    }

    /**
     * 친구 요청을 보내기 위해 특정 사용자의 닉네임으로 사용자를 검색함
     *
     * @param nickname 친구요청을 보내고픈 사용자의 닉네임
     * @return 해당 닉네임을 가진 사용자의 정보를 담은 {@link UserInfoDto} DTO
     */
    @GetMapping("/requests/search")
    public Response<UserInfoDto> searchUserByNickname(@RequestParam String nickname) {
        UserInfoDto response = friendService.searchUserByNickname(nickname);
        return Response.success(response);
    }

    /**
     * 특정 사용자에게 친구 요청 보내기
     *
     * @param id 현재 로그인한 회원 ID
     * @param request 친구 요청을 보내고픈 유저의 id를 담은 {@link RequestFriendRequest} VO
     * @return 친구 요청 성공 메세지를 담은 {@link MessageResponse}
     */
    @PostMapping("/requests")
    public Response<MessageResponse> requestFriend(
            @AuthenticationPrincipal Long id,
            @RequestBody RequestFriendRequest request
    ) {
        MessageResponse response = friendService.requestFriend(id, request);
        return Response.success(response);
    }

    /**
     * 특정 사용자의 친구 요청 수락하기
     *
     * @param currentUserId 현재 로그인한 회원 ID
     * @param id 친구 요청을 수락하고픈 유저의 id
     * @return 친구 요청 수락 성공 메세지를 담은 {@link MessageResponse}
     */
    @PostMapping("/requests/{id}")
    public Response<MessageResponse> requestFriendRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id
    ) {
        MessageResponse response = friendService.acceptFriendRequest(currentUserId, id);
        return Response.success(response);
    }

    /**
     * 특정 사용자의 친구 요청 거절하기
     *
     * @param currentUserId 현재 로그인한 회원 ID
     * @param id 친구 요청을 거절하고픈 유저의 id
     * @return 친구 요청 거절 성공 메세지를 담은 {@link MessageResponse}
     */
    @DeleteMapping("/requests/{id}")
    public Response<MessageResponse> denyFriendRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id
    ) {
        MessageResponse response = friendService.denyFiendRequest(currentUserId, id);
        return Response.success(response);
    }
}
