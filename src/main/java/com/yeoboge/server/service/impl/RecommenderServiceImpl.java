package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.recommender.*;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
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
    public RecommendForSingleResponse getSingleRecommendation(Long userId) {
        List<Genre> favoriteGenres = recommendRepository.getMyFavoriteGenre(userId);

        List<RecommendedBySomething> recommenderList = getRecommenderList(userId);
        recommenderList.addAll(getGenreRecommender(userId, favoriteGenres));

        return runAsyncJobsForRecommendation(recommenderList);
    }

    private List<RecommendedBySomething> getGenreRecommender(long userId, List<Genre> genres) {
        long numRating = ratingRepository.countByUser(userId);

        List<RecommendedBySomething> genreRecommenders = new ArrayList<>();
        for (Genre genre : genres) {
            RecommendedBySomething recommender = numRating >= 10
                    ? RecommendedByGenreMl.builder()
                        .repository(recommendRepository)
                        .type(RecommendTypes.FAVORITE_GENRE)
                        .client(webClient)
                        .userId(userId)
                        .genreId(genre.getId())
                        .genreName(genre.getName())
                        .build()
                    : RecommendedByGenreSql.builder()
                        .repository(recommendRepository)
                        .type(RecommendTypes.FAVORITE_GENRE)
                        .genreId(genre.getId())
                        .genreName(genre.getName())
                        .build();
            genreRecommenders.add(recommender);
        }

        return genreRecommenders;
    }

    private List<RecommendedBySomething> getRecommenderList(long userId) {
        List<RecommendedBySomething> recommenderList = new ArrayList<>();

        recommenderList.add(RecommendedByBookmark.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.MY_BOOKMARK)
                .userId(userId)
                .build());
        recommenderList.add(RecommendedByFriends.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.FRIENDS_FAVORITE)
                .userId(userId)
                .build());
        recommenderList.add(RecommendedByTop10.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.TOP_10)
                .build());

        return recommenderList;
    }

    private RecommendForSingleResponse runAsyncJobsForRecommendation(List<RecommendedBySomething> recommenders) {
        RecommendForSingleResponse response = new RecommendForSingleResponse(
                new ConcurrentLinkedQueue<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>()
        );
        CountDownLatch latch = new CountDownLatch(recommenders.size());

        for (RecommendedBySomething recommender : recommenders)
            recommender.addRecommendedDataToResponse(response, latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
