package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByGenre extends RecommendedBySomethingBase implements RecommendedBySomething {
    private long genreId;

    @Builder
    public RecommendedByGenre(
            RecommendRepository repository,
            RecommendTypes type,
            long genreId,
            String userNickname,
            String genreName
    ) {
        super(repository, type);
        this.genreId = genreId;
        this.key = type.getKey() + genreName;
        this.description = userNickname + "님이 좋아하는 " + genreName + " 보드게임 🎲";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        setAsyncProcessing(CompletableFuture.supplyAsync(
                () -> repository.getPopularBoardGamesOfFavoriteGenre(genreId)
        ), response, latch);
    }
}
