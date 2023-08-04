package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.user.BookmarkResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 보드게임 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 */
@RestController
@RequestMapping("/boardgames")
@RequiredArgsConstructor
public class BoardGameController {
    private final BoardGameService boardGameService;

    /**
     * 보드게임 단건 조회 API
     *
     * @param id 조회하고자 하는 보드게임의 id
     * @return 보드게임의 정보에 대한 {@link BoardGameDetailResponse} VO를 리턴함
     */
    @GetMapping("/{id}")
    public Response<BoardGameDetailResponse> test(@PathVariable Long id) {
        BoardGameDetailResponse response = boardGameService.getBoardGameDetail(id);
        return Response.success(response);
    }

    /**
     * 특정 보드게임을 찜하는 API
     *
     * @param id {@link com.yeoboge.server.domain.entity.BoardGame} ID
     * @param userId {@link com.yeoboge.server.domain.entity.User} ID
     * @return 찜하기 성공 메세지를 포함한 HTTP 200 응답
     */
    @PostMapping("/{id}/bookmarks")
    public ResponseEntity<Response<BookmarkResponse>> addBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        BookmarkResponse response = boardGameService.addBookmark(id, userId);
        return Response.created(response);
    }
}
