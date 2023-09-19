package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.dto.recommend.RecommendWithGenreRequest;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendedByGenreMl extends RecommendedByML {
    @Builder
    public RecommendedByGenreMl(
            RecommendRepository repository,
            RecommendTypes type,
            WebClient client,
            long userId,
            long genreId,
            String genreName
    ) {
        super(repository, type, client);
        this.key = type.getKey() + genreName;
        this.description =  "내가 좋아하는 " + genreName + " 보드게임 🎲";
        this.requestBody = new RecommendWithGenreRequest(userId, genreId);
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        final String endPoint = "/recommends";
        Mono<RecommendWebClientResponse> mono = getWebClientMono(RecommendWebClientResponse.class, endPoint);
        mono.subscribe(wr -> {
            List<BoardGameThumbnailDto> boardGames = repository.getRecommendedBoardGames(wr.result());
            addToResponse(response, boardGames);
            latch.countDown();
        });
    }
}
