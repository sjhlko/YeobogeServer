package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendedByML extends RecommendedBySomethingBase {
    private Mono<RecommendWebClientResponse> mono;

    RecommendedByML(RecommendRepository repository, Mono<RecommendWebClientResponse> mono, RecommendTypes type) {
        super(repository, type);
        this.mono = mono;
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        mono.subscribe(wr -> {
            List<BoardGameThumbnailDto> boardGames = repository.getRecommendedBoardGames(wr.result());
            addToResponse(response, boardGames);
            latch.countDown();
        });
    }
}
