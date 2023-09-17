package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByTop10 extends RecommendedBySQL {
    @Builder
    public RecommendedByTop10(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
        this.description = "현재 인기 보드게임 🌟";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getTopTenBoardGames());
        setAsyncProcessing(response, latch);
    }
}
