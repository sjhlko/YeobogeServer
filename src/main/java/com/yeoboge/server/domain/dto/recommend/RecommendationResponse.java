package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;

import java.util.List;

public interface RecommendationResponse {
    default void addRecommendationsForIndividual(List<BoardGameThumbnailDto> boardGames, String key, String description) {}

    default void addRecommendationsForGroup(List<BoardGameDetailedThumbnailDto> boardGames) {}
}
