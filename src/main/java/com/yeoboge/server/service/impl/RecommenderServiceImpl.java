package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.helper.recommender.IndividualRecommenderFactoryImpl;
import com.yeoboge.server.helper.recommender.IndividualRecommender;
import com.yeoboge.server.helper.recommender.IndividualRecommenderFactory;
import com.yeoboge.server.helper.utils.ThreadUtils;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.*;

/**
 * {@link RecommenderService} 구현체
 */
@Service
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {
    private final RecommendRepository recommendRepository;
    private final RatingRepository ratingRepository;

    private final WebClient webClient;

    @Override
    public IndividualRecommendationResponse getSingleRecommendation(Long userId) {
        List<Genre> favoriteGenres = recommendRepository.getMyFavoriteGenre(userId);
        long numRating = ratingRepository.countByUser(userId);

        IndividualRecommenderFactory recommenderFactory = IndividualRecommenderFactoryImpl.builder()
                .repository(recommendRepository)
                .userId(userId)
                .genres(favoriteGenres)
                .numRating(numRating)
                .build();
        List<IndividualRecommender> recommenderList = recommenderFactory.getRecommenders(webClient);

        return runAsyncJobsForRecommendation(recommenderList);
    }

    /**
     * 사용자에게 추천할 보드게임 카테고리마다 추천 목록을 생성후 해당 메타데이터를 {@link IndividualRecommendationResponse}에 추가함.
     * 그리고 모든 로직은 비동기로 동작되므로 모든 로직이 완료될 때까지 대기한 뒤 {@link IndividualRecommendationResponse}를 반환함.
     *
     * @param recommenders 카테고리별 추천 목록 생성 메서드를 제공하는 {@link IndividualRecommender} 리스트
     * @return 추천할 보드게임 목록의 메타데이터들이 담긴 {@link IndividualRecommendationResponse}
     */
    private IndividualRecommendationResponse runAsyncJobsForRecommendation(
            List<IndividualRecommender> recommenders
    ) {
        IndividualRecommendationResponse response = new IndividualRecommendationResponse(
                new ConcurrentLinkedQueue<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>()
        );
        CountDownLatch latch = ThreadUtils.getThreadAwaiter(recommenders.size());

        for (IndividualRecommender recommender : recommenders)
            recommender.addRecommendationsToResponse(response, latch);

        ThreadUtils.awaitUntilJobsDone(latch);

        return response;
    }
}
