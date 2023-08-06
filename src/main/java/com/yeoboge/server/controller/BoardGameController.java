package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.vo.response.MessageResponse;
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
     * @param id 찜할 보드게임 ID
     * @param userId 회원 ID
     * @return 찜하기 성공 메세지를 포함한 HTTP 200 응답
     */
    @PostMapping("/{id}/bookmarks")
    public ResponseEntity<Response<MessageResponse>> addBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        MessageResponse response = boardGameService.addBookmark(id, userId);
        return Response.created(response);
    }

    /**
     * 특정 보드게임에 대해 찜하기를 취소하는 API
     *
     * @param id 찜하기 취소할 보드게임 ID
     * @param userId 취소할 사용자 ID
     * @return HTTP 204 응답
     */
    @DeleteMapping("/{id}/bookmarks")
    public ResponseEntity removeBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        boardGameService.removeBookmark(id, userId);
        return Response.deleted();
    }

    /**
     * 보드게임을 평가하는 API
     *
     * @param id 평가할 보드게임 ID
     * @param userId 평점을 남길 사용자 ID
     * @param rate 보드게임 평점
     * @return 평점 저장 메세지를 포함한 HTTP 200 응답
     */
    @PutMapping("/{id}/rate")
    public Response<MessageResponse> rateBoardGame(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestParam Double rate
    ) {
        MessageResponse response = boardGameService.rateBoardGame(id, userId, rate);
        return Response.success(response);
    }
}
