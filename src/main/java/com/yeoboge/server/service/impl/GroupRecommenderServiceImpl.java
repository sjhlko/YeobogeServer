package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.GroupErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.utils.ThreadUtils;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.GroupRecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final FriendRepository friendRepository;

    @Override
    public GroupMembersResponse getGroupMembers(long userId, UserGpsDto gpsDto) {
        userPool.computeIfAbsent(gpsDto, k -> new ConcurrentSkipListSet<>()).add(userId);

        ThreadUtils.waitForSeconds(5000L);
        List<UserInfoDto> groupMembers = findGroupMembers(userId, gpsDto);

        ThreadUtils.waitForSeconds(2000L);
        removeFromUserPool(userId, gpsDto);

        return GroupMembersResponse.builder().group(groupMembers).build();
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
