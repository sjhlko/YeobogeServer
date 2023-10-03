package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.enums.error.GroupErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.recommender.*;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * {@link RecommenderService} 구현체
 */
@Service
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {
    private final Map<UserGpsDto, Set<Long>> userPool = new ConcurrentHashMap<>();

    private final RecommendRepository recommendRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    private final WebClient webClient;

    @Override
    public RecommendForSingleResponse getSingleRecommendation(Long userId) {
        List<Genre> favoriteGenres = recommendRepository.getMyFavoriteGenre(userId);

        List<RecommendedBySomething> recommenderList = getRecommenderList(userId);
        recommenderList.addAll(getGenreRecommender(userId, favoriteGenres));

        return runAsyncJobsForRecommendation(recommenderList);
    }

    @Override
    public GroupMembersResponse getGroupMembers(long userId, UserGpsDto gpsDto) {
        userPool.computeIfAbsent(gpsDto, k -> new ConcurrentSkipListSet<>()).add(userId);

        waitForSeconds(5000L);
        List<UserInfoDto> groupMembers = findGroupMembers(userId, gpsDto);

        waitForSeconds(2000L);
        removeFromUserPool(userId, gpsDto);

        return GroupMembersResponse.builder().group(groupMembers).build();
    }

    /**
     * 사용자가 선호하는 장르에 해당하는 추천 보드게임 목록을 생성할 {@link RecommendedBySomething} 리스트를 반환함.
     *
     * @param userId 추천 목록을 제공할 사용자 ID
     * @param genres 사용자가 선호하는 보드게임 장르 ID 리스트
     * @return 해당 장르 별 추천 목록을 생성할 {@link RecommendedBySomething} 리스트
     */
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

    /**
     * AI 생성 관련 로직이 아닌 DB 조회 로직에만 해당하는 카테고리들의 {@link RecommendedBySomething} 리스트를 반환함.
     *
     * @param userId 추천 목록을 제공할 사용자 ID
     * @return DB 조회 로직과 관련한 {@link RecommendedBySomething} 리스트
     */
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

    /**
     * 사용자에게 추천할 보드게임 카테고리마다 추천 목록을 생성후 해당 메타데이터를 {@link RecommendForSingleResponse}에 추가함.
     * 그리고 모든 로직은 비동기로 동작되므로 모든 로직이 완료될 때까지 대기한 뒤 {@link RecommendForSingleResponse}를 반환함.
     *
     * @param recommenders 카테고리별 추천 목록 생성 메서드를 제공하는 {@link RecommendedBySomething} 리스트
     * @return 추천할 보드게임 목록의 메타데이터들이 담긴 {@link RecommendForSingleResponse}
     */
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

    /**
     * 특정 시간 동안 스레드의 동작을 대기시킴.
     *
     * @param milliSeconds 스레드가 대기할 시간
     */
    private void waitForSeconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code userPool}에서 사용자와 같은 위치인 친구들만 그룹에 포함시킴.
     *
     * @param userId 그룹 맺기를 요청한 사용자 ID
     * @param gpsDto 사용자의 현재 GPS 정보가 담긴 {@link UserGpsDto}
     * @return 사용자와 매칭된 그룹 구성원들의 {@link UserInfoDto} 리스트
     */
    private List<UserInfoDto> findGroupMembers(long userId, UserGpsDto gpsDto) {
        Set<Long> usersInSameArea = userPool.get(gpsDto);
        List<Long> groupIds = friendRepository.findFriendIdsInIdList(userId, usersInSameArea.stream().toList());

        if (groupIds.isEmpty())
            throw new AppException(GroupErrorCode.NO_GROUP_MEMBER);

        groupIds.add(0, userId);
        if (groupIds.size() >= 10)
            throw new AppException(GroupErrorCode.OVER_LIMIT_GROUP_MEMBER);

        List<UserInfoDto> userInfos = new ArrayList<>();
        for (long id : groupIds) {
            User user = userRepository.getById(id);
            userInfos.add(UserInfoDto.of(user));
        }

        return userInfos;
    }

    /**
     * 그룹 매칭 후 사용자를 {@code userPool}에서 제거함.
     *
     * @param userId 제거할 사용자 ID
     * @param gpsDto 사용자의 현재 GPS {@link UserGpsDto}
     */
    private void removeFromUserPool(long userId, UserGpsDto gpsDto) {
        Set<Long> usersInSameArea = userPool.get(gpsDto);
        usersInSameArea.remove(userId);
        userPool.put(gpsDto, usersInSameArea);
        if (userPool.get(gpsDto).isEmpty()) userPool.remove(gpsDto);
    }
}