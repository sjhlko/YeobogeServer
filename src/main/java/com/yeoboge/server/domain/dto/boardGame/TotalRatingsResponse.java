package com.yeoboge.server.domain.dto.boardGame;

import java.util.List;
import java.util.Map;

/**
 * 사용자가 평가한 보드게임 목록을 각 별점 별로 분류해
 * {@link Map}에 담아 전달하는 보드게임 썸네일 목록
 *
 * @see BoardGameThumbnailDto
 */
public class TotalRatingsResponse extends AbstractBoardGameMapResponse<BoardGameThumbnailDto> {
    @Override
    public void addBoardGames(List<BoardGameThumbnailDto> boardGames, Object key) {
        this.boardGames.put(key, boardGames);
    }
}
