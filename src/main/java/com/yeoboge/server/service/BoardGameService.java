package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.user.BookmarkResponse;
import com.yeoboge.server.domain.vo.response.MessageResponse;

import java.io.IOException;

public interface BoardGameService {
    void saveBoardGame() throws IOException;
    BoardGameDetailResponse getBoardGameDetail(Long id);

    /**
     * 특정 보드게임을 찜 목록에 추가함.
     *
     * @param id     {@link com.yeoboge.server.domain.entity.BoardGame} ID
     * @param userId {@link com.yeoboge.server.domain.entity.User} ID
     * @return 찜하기 성공 메세지 {@link MessageResponse}
     */
    BookmarkResponse addBookmark(Long id, Long userId);
}
