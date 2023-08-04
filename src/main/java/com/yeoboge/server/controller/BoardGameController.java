package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
