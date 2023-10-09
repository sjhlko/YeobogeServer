package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;

/**
 * 그룹 추천 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface GroupRecommenderService {
    /**
     * 사용자와 현재 같은 위치인 친구들과 그룹을 맺음.
     *
     * @param userId 그룹 맺기를 요청한 사용자 ID
     * @param gpsDto 사용자의 현재 GPS 정보가 담긴 {@link UserGpsDto}
     * @return 사용자와 매칭된 그룹에 대한 {@link GroupMembersResponse}
     */
    GroupMembersResponse getGroupMembers(long userId, UserGpsDto gpsDto);
}
