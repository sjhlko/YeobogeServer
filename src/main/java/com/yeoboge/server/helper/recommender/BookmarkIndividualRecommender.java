package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 사용자가 찜한 보드게임에서 일부를 선택해 추천 목록을 생성하는 로직을 구현한 클래스
 */
public class BookmarkIndividualRecommender extends AbstractIndividualSQLRecommender {
    private long userId;

    @Builder
    public BookmarkIndividualRecommender(
            RecommendRepository repository, RecommendTypes type, long userId
    ) {
        super(repository, type);
        this.userId = userId;
        this.description = "내가 찜한 보드게임 🔖";
    }

    @Override
    public void addRecommendationsToResponse(
            IndividualRecommendationResponse response, CountDownLatch latch
    ) {
        this.future = CompletableFuture.supplyAsync(
                () -> repository.getMyBookmarkedBoardGames(userId)
        );
        setAsyncProcessing(response, latch);
    }
}
