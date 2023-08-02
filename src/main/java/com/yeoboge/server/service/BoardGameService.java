package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;

import java.io.IOException;

public interface BoardGameService {
    void saveBoardGame() throws IOException;
    BoardGameDetailResponse getBoardGameDetail(Long id);
}
