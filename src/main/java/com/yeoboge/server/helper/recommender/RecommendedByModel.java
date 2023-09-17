package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendedByModel extends RecommendedBySomethingBase implements RecommendedBySomething {
    private Mono<RecommendWebClientResponse> mono;

    @Builder
    public RecommendedByModel(
            RecommendRepository  repository,
            RecommendTypes type,
            Mono<RecommendWebClientResponse> mono,
            String userNickname
    ) {
        super(repository, type);
        this.mono = mono;
        this.description = userNickname + "님만을 위한 추천 💘";
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
