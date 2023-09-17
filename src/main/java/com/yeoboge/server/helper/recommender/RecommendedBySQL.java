package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public abstract class RecommendedBySQL extends RecommendedBySomethingBase {
    protected CompletableFuture<List<BoardGameThumbnailDto>> future;

    RecommendedBySQL(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
    }

    protected void setAsyncProcessing(RecommendForSingleResponse response, CountDownLatch latch) {
        future.thenCompose(
                boardGames -> CompletableFuture.runAsync(() -> addToResponse(response, boardGames))
        ).thenRun(() -> latch.countDown());
    }
}
