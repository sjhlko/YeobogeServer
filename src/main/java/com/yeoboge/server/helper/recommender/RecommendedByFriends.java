package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 사용자의 친구들이 높게 평가한 보드게임 목록을 토대로
 * 추천 목록을 생성하는 로직을 구현한 클래스
 */
public class RecommendedByFriends extends RecommendedBySQL {
    private long userId;

    @Builder
    public RecommendedByFriends(RecommendRepository repository, RecommendTypes type, long userId) {
        super(repository, type);
        this.userId = userId;
        this.description =  "친구들이 좋아하는 보드게임 👥";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getFavoriteBoardGamesOfFriends(userId));
        setAsyncProcessing(response, latch);
    }
}
