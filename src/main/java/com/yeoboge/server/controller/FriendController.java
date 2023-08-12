package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
