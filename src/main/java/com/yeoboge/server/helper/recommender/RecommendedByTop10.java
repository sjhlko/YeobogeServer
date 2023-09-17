package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByTop10 extends RecommendedBySomethingBase implements RecommendedBySomething {
    @Builder
    public RecommendedByTop10(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
        this.description = "현재 인기 보드게임 🌟";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        setAsyncProcessing(CompletableFuture.supplyAsync(
                () -> repository.getTopTenBoardGames()
        ), response, latch);
    }
}
