package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class RecommendedBySomethingBase implements RecommendedBySomething {
    protected RecommendRepository repository;
    protected String key;
    protected String description;

    RecommendedBySomethingBase(RecommendRepository repository, RecommendTypes type) {
        this.repository = repository;
        this.key = type.getKey();
    }

    protected void addToResponse(RecommendForSingleResponse response, List<BoardGameThumbnailDto> boardGames) {
        if (boardGames.isEmpty()) return;

        response.keys().add(key);
        response.shelves().put(key, boardGames);
        response.descriptions().put(key, description);
    }

    public abstract void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch);
}
