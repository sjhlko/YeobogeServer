package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 보드게임 썸네일에 해당하는 데이터들을 {@link Map}에 담아 각 특성에 맞게
 * 분류해서 저장한 뒤 넘겨주는 보드게임 목록
 *
 * @see BoardGameThumbnailDto
 * @param boardGames 보드게임 목록을 저장할 {@link Map}
 */
public record ThumbnailMapResponse(
        Map<Object, List<BoardGameThumbnailDto>> boardGames
) implements BoardGameListResponse<BoardGameThumbnailDto> {

    @Override
    public void addBoardGames(List<BoardGame> boardGames, Object key) {
        List<BoardGameThumbnailDto> thumbnails = new ArrayList<>();

        for (BoardGame boardGame : boardGames) {
            BoardGameThumbnailDto thumbnail = BoardGameThumbnailDto.of(boardGame);
            thumbnails.add(thumbnail);
        }

        this.boardGames.put(key, thumbnails);
    }
}
