package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.user.BookmarkResponse;
import com.yeoboge.server.domain.vo.response.MessageResponse;

import java.io.IOException;

/**
 * 보드게임 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface BoardGameService {

    /**
     * 보드게임을 저장하는 메서드
     */
    void saveBoardGame() throws IOException;

    /**
     * 특정 보드게임에 대한 정보를 반환하는 메서드
     *
     * @param id 정보를 반환할 보드게임의 id
     * @return {@link BoardGameDetailResponse}
     */
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
