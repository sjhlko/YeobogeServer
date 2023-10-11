package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;

import java.util.List;

public record GroupRecommendationResponse(List<BoardGameDetailedThumbnailDto> recommendations) {
    public void addRecommendationBoardGames(List<BoardGameDetailedThumbnailDto> boardGames) {
        recommendations.addAll(boardGames);
    }
}
