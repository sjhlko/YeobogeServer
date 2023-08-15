package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @return 해당 닉네임을 가진 사용자의 정보를 담은 {@link FriendInfoDto} DTO
     */
    @GetMapping("/requests/search")
    public Response<FriendInfoDto> searchUserByNickname(@RequestParam String nickname) {
        FriendInfoDto response = friendService.searchUserByNickname(nickname);
        return Response.success(response);
    }
}
