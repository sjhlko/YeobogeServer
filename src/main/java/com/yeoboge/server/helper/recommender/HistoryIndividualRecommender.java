package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 사용자가 가장 최근 그룹으로 추천 받은 보드게임 목록으로
 * 추천 리스트를 생성하는 로직을 구현한 {@link IndividualRecommender} 구현체
 */
public class HistoryIndividualRecommender extends AbstractIndividualSQLRecommender {
    private long userId;

    @Builder
    public HistoryIndividualRecommender(
            RecommendRepository repository,
            RecommendTypes type,
            long userId) {
        super(repository, type);
        this.userId = userId;
        this.description = "최근 그룹으로 추천 받은 보드게임 🎁";
    }

    @Override
    public void addRecommendationsToResponse(
            IndividualRecommendationResponse response, CountDownLatch latch
    ) {
        this.future = CompletableFuture.supplyAsync(
                () -> repository.getRecommendationHistories(userId)
        );
        setAsyncProcessing(response, latch);
    }
}
