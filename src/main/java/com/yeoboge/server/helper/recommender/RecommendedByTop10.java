package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 장르 통합 인기 보드게임 목록을 추천 결과에 저장하는 구현한 클래스
 */
public class RecommendedByTop10 extends RecommendedBySQL {
    @Builder
    public RecommendedByTop10(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
        this.description = "현재 인기 보드게임 🌟";
    }

    @Override
    public void addRecommendedDataToResponse(IndividualRecommendationResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getTopTenBoardGames());
        setAsyncProcessing(response, latch);
    }
}
