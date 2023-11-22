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
 * 선호하는 장르 별 추천 목록을 생성하는 로직을 구현한 {@link IndividualRecommender} 구현체
 */
public class AIIndividualRecommender extends AbstractIndividualRecommender {
    private final String END_POINT = "/recommends";
    private WebClient client;
    private RecommendWithGenreRequest requestBody;
    private RecommendTypes type;

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
        this.type = type;
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
        handleEmptyMono(mono, response, latch);
    }

    /**
     * 외부 API 요청 중 에러 발생 시 맞춤 추천 목록 대신
     * 인기 보드게임을 추천하기 위해 {@link GenreIndividualRecommender}를 대신 호출함.
     *
     * @param mono 외부 API를 호출하고 받은 {@link Mono}
     * @param response {@link IndividualRecommendationResponse}
     * @param latch 비동기 작업이 완료 될 때까지 대기하기 위한 {@link CountDownLatch}
     */
    private void handleEmptyMono(Mono<?> mono, IndividualRecommendationResponse response, CountDownLatch latch) {
        if (Boolean.TRUE.equals(mono.hasElement().block())) return;

        long genreId = requestBody.genre_id();
        String genreName = key.substring(type.getKey().length());
        GenreIndividualRecommender alterRecommender = new GenreIndividualRecommender(
                repository, type, genreId, genreName
        );
        alterRecommender.addRecommendationsToResponse(response, latch);
    }
}
