package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByBookmark extends RecommendedBySQL {
    private long userId;

    @Builder
    public RecommendedByBookmark(
            RecommendRepository repository, RecommendTypes type, long userId
    ) {
        super(repository, type);
        this.userId = userId;
        this.description = "내가 찜한 보드게임 🔖";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getMyBookmarkedBoardGames(userId));
        setAsyncProcessing(response, latch);
    }
}
