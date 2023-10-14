package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 평가한 보드게임이 적은 초기 사용자에게 선호하는 장르 별
 * 인기 보드게임 목록을 생성하는 로직을 구현한 클래스
 */
public class GenreIndividualRecommender extends AbstractIndividualSQLRecommender {
    private long genreId;

    @Builder
    public GenreIndividualRecommender(
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
    public void addRecommendationsToResponse(
            IndividualRecommendationResponse response, CountDownLatch latch
    ) {
        this.future = CompletableFuture.supplyAsync(
                () -> repository.getPopularBoardGamesOfGenreForIndividual(genreId)
        );
        setAsyncProcessing(response, latch);
    }
}
