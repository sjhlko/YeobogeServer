package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.domain.dto.recommend.RecommendWithGenreRequest;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.helper.utils.WebClientUtils;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 보드게임 평가 데이터가 쌓인 사용자에 대해 AI 모델을 통해
 * 선호하는 장르 별 추천 목록을 생성하는 로직을 구현한 클래스
 */
public class AIIndividualRecommender extends AbstractIndividualRecommender {
    private final String END_POINT = "/recommend";
    private WebClient client;
    protected RecommendWithGenreRequest requestBody;

    @Builder
    public AIIndividualRecommender(
            RecommendRepository repository,
            RecommendTypes type,
            WebClient client,
            long userId,
            long genreId,
            String genreName
    ) {
        super(repository, type);
        this.client = client;
        this.key = type.getKey() + genreName;
        this.description =  "내가 좋아하는 " + genreName + " 보드게임 🎲";
        this.requestBody = new RecommendWithGenreRequest(userId, genreId);
    }

    @Override
    public void addRecommendationsToResponse(
            IndividualRecommendationResponse response, CountDownLatch latch
    ) {
        Mono<RecommendWebClientResponse> mono = WebClientUtils.post(
                client, RecommendWebClientResponse.class, requestBody, END_POINT
        );
        mono.subscribe(wr -> {
            List<BoardGameThumbnailDto> boardGames =
                    repository.getRecommendedBoardGamesForIndividual(wr.result());
            response.addRecommendations(boardGames, key, description);
            latch.countDown();
        });
    }
}
