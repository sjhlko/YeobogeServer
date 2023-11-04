package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.boardGame.RatingRequest;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
     * 보드게임 상세 조회 API
     *
     * @param userId 조회를 요청한 사용자 ID
     * @param boardGameId 조회하고자 하는 보드게임의 ID
     * @return 보드게임의 정보에 대한 {@link BoardGameDetailResponse} VO를 리턴함
     */
    @GetMapping("/{boardGameId}")
    public Response<BoardGameDetailResponse> getBoardGameDetail(
            @PathVariable long boardGameId, @AuthenticationPrincipal long userId
    ) {
        BoardGameDetailResponse response = boardGameService.getBoardGameDetail(userId, boardGameId);
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
     * @param request 보드게임 평점 {@link RatingRequest}
     * @return 평점 저장 메세지를 포함한 HTTP 200 응답
     */
    @PutMapping("/{id}/rate")
    public Response<MessageResponse> rateBoardGame(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody RatingRequest request
    ) {
        MessageResponse response = boardGameService.rateBoardGame(id, userId, request);
        return Response.success(response);
    }

    /**
     * 보드게임 검색 API
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @param request 보드게임 검색 조건이 담긴 {@link SearchBoardGameRequest} VO
     * @return 보드게임 검색 결과에 대한 정보가 담긴 {@link BoardGameDetailedThumbnailDto} DTO
     */
    @GetMapping("/search")
    public Response<PageResponse> searchBoardGame(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            SearchBoardGameRequest request
    ) {
        PageResponse response = boardGameService.searchBoardGame(pageable, request);
        return Response.success(response);
    }
}
