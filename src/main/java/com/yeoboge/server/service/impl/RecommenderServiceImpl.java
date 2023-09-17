package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.vo.recommend.*;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.recommender.*;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.RecommenderService;
import com.yeoboge.server.helper.utils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final UserRepository userRepository;

    private final WebClient webClient;

    @Override
    public RecommendForSingleResponse getSingleRecommendation(Long userId) {
        List<Genre> favoriteGenres = recommendRepository.getMyFavoriteGenre(userId);
        String nickname = userRepository.getById(userId).getNickname();

        List<RecommendedBySomething> recommenderList = getRecommenderList(userId, nickname, favoriteGenres);
        recommenderList.add(getModelRecommender(userId, nickname));

        return runAsyncJobsForRecommendation(recommenderList);
    }

    private RecommendedByModel getModelRecommender(long userId, String nickname) {
        final String endPoint = "/recommends/{id}";
        Mono<RecommendWebClientResponse> mono = WebClientUtils.get(
                webClient, RecommendWebClientResponse.class, endPoint, userId
        );

        return RecommendedByModel.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.PERSONAL_RECOMMEND)
                .mono(mono)
                .userNickname(nickname)
                .build();
    }

    private List<RecommendedBySomething> getRecommenderList(long userId, String nickname, List<Genre> genres) {
        List<RecommendedBySomething> recommenderList = new ArrayList<>();

        recommenderList.add(RecommendedByBookmark.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.MY_BOOKMARK)
                .userId(userId)
                .userNickname(nickname)
                .build());
        recommenderList.add(RecommendedByFriends.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.FRIENDS_FAVORITE)
                .userId(userId)
                .userNickname(nickname)
                .build());
        recommenderList.add(RecommendedByTop10.builder()
                .repository(recommendRepository)
                .type(RecommendTypes.TOP_10)
                .build());
        for (Genre genre : genres)
            recommenderList.add(RecommendedByGenre.builder()
                    .repository(recommendRepository)
                    .type(RecommendTypes.FAVORITE_GENRE)
                    .userNickname(nickname)
                    .genreId(genre.getId())
                    .genreName(genre.getName())
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
