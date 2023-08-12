package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;

import java.util.List;

/**
 * 찜한 보드게임 조회 시 넘겨주는 보드게임 목록
 *
 * @param boardGames 찜한 보드게임의 ID, 이름, 썸네일 URL이 포함된 리스트
 * @see BoardGameThumbnail
 */
public record ThumbnailListResponse(
        List<BoardGameThumbnail> boardGames
) implements BoardGameListResponse<BoardGameThumbnail> {

    @Override
    public void addBoardGames(List<BoardGame> boardGames) {
        for (BoardGame boardGame : boardGames) {
            BoardGameThumbnail thumbnail = BoardGameThumbnail.of(boardGame);
            this.boardGames.add(thumbnail);
        }
    }
}
