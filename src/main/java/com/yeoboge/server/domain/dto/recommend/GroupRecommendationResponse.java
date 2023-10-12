package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;

import java.util.List;

public record GroupRecommendationResponse(
        List<BoardGameDetailedThumbnailDto> recommendations
) implements RecommendationResponse {
    @Override
    public void addRecommendationsForGroup(List<BoardGameDetailedThumbnailDto> boardGames) {
        recommendations.addAll(boardGames);
    }
}
