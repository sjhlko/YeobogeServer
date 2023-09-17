package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByFriends extends RecommendedBySQL {
    private long userId;

    @Builder
    public RecommendedByFriends(
            RecommendRepository repository, RecommendTypes type, long userId, String userNickname
    ) {
        super(repository, type);
        this.userId = userId;
        this.description = userNickname + "님의 친구들이 좋아하는 보드게임 👥";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getFavoriteBoardGamesOfFriends(userId));
        setAsyncProcessing(response, latch);
    }
}
