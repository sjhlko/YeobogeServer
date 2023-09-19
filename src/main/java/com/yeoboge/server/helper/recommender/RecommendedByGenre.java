package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class RecommendedByGenre extends RecommendedBySQL {
    private long genreId;

    @Builder
    public RecommendedByGenre(
            RecommendRepository repository,
            RecommendTypes type,
            long genreId,
            String genreName
    ) {
        super(repository, type);
        this.genreId = genreId;
        this.key = type.getKey() + genreName;
        this.description =  "내가 좋아하는 " + genreName + " 보드게임 🎲";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        this.future = CompletableFuture.supplyAsync(() -> repository.getPopularBoardGamesOfFavoriteGenre(genreId));
        setAsyncProcessing(response, latch);
    }
}
