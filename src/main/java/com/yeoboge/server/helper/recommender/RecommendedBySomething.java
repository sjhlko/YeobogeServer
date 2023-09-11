package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;

import java.util.List;

public interface RecommendedBySomething {
    String getKey();

    String getDescription();

    List<BoardGameThumbnailDto> getRecommendedThumbnailList();
}
