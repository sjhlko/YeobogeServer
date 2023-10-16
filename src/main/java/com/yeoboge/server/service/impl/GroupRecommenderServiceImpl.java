package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.enums.error.GroupErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.recommender.GroupRecommender;
import com.yeoboge.server.helper.recommender.GroupRecommenderFactory;
import com.yeoboge.server.helper.recommender.GroupRecommenderFactoryImpl;
import com.yeoboge.server.helper.utils.ThreadUtils;
import com.yeoboge.server.repository.*;
import com.yeoboge.server.service.GroupRecommenderService;
import com.yeoboge.server.service.PushAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * {@link GroupRecommenderService} 구현체
 */
@Service
@RequiredArgsConstructor
public class GroupRecommenderServiceImpl implements GroupRecommenderService {
    private final Map<UserGpsDto, Set<Long>> userPool = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final FriendRepository friendRepository;
    private final RecommendRepository recommendRepository;
    private final PushAlarmService pushAlarmService;
    private final TokenRepository tokenRepository;

    private final WebClient webClient;

    @Override
    public GroupMembersResponse getGroupMembers(long userId, UserGpsDto gpsDto) {
        userPool.computeIfAbsent(gpsDto, k -> new ConcurrentSkipListSet<>()).add(userId);

        ThreadUtils.waitForSeconds(3000L);
        List<UserInfoDto> groupMembers = findGroupMembers(userId, gpsDto);

        ThreadUtils.waitForSeconds(2000L);
        removeFromUserPool(userId, gpsDto);

        return GroupMembersResponse.builder().group(groupMembers).build();
    }

    @Override
    public GroupRecommendationResponse getGroupRecommendation(GroupRecommendationRequest request) {
        List<Long> recommendableMembers = findRecommendableMembers(request.members());
        sendPushAlarmForGroupRecommendation(request.members());

        GroupRecommenderFactory factory = GroupRecommenderFactoryImpl.builder()
                .recommendableMembers(recommendableMembers)
                .repository(recommendRepository)
                .request(request)
                .build();
        GroupRecommender recommender = factory.getRecommender(webClient);

        return getRecommendationResponse(recommender);
    }

    /**
     * 그룹 구성원 중 AI 모델을 통해 추천을 받을 수 있는 구성원만 필터링함.
     *
     * @param members 기존 추천을 요청한 전체 그룹 구성원 ID 리스트
     * @return AI 추천을 받을 수 있는 그룹 구성원 ID 리스트
     */
    private List<Long> findRecommendableMembers(List<Long> members) {
        List<Long> recommendableMembers = new ArrayList<>();

        for (long memberId : members) {
            long numRating = ratingRepository.countByUser(memberId);
            if (numRating >= 10) recommendableMembers.add(memberId);
        }

        return recommendableMembers;
    }

    /**
     * {@link GroupRecommender}에서 생성한 추천 보드게임 목록을 {@link GroupRecommendationResponse}에 담아 반환함.
     *
     * @param recommender 추천 목록 생성 로직을 담당하는 {@link GroupRecommender}
     * @return 추천 보드게임 목록이 담긴 {@link GroupRecommendationResponse}
     */
    private GroupRecommendationResponse getRecommendationResponse(GroupRecommender recommender) {
        GroupRecommendationResponse response = new GroupRecommendationResponse(new ArrayList<>());
        recommender.addRecommendationsToResponse(response);

        return response;
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

    private void sendPushAlarmForGroupRecommendation(List<Long> users) {
        for (Long user : users) {
            Optional<String> fcmToken = tokenRepository.findFcmToken(user);
            if (fcmToken.isPresent()) {
                PushAlarmRequest pushAlarmRequest = PushAlarmRequest.builder()
                        .pushAlarmType(PushAlarmType.RATING)
                        .targetToken(fcmToken.get())
                        .build();
                pushAlarmService.sendPushAlarm(pushAlarmRequest, 10000);
            }
        }
    }
}
