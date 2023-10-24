package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import org.springframework.data.domain.Pageable;

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

    /**
     * 그룹 구성원들에게 적절한 추천 보드게임 목록을 생성해 응답 DTO에 담아 반환함.
     *
     * @param userId 추천을 요청한 사용자 ID
     * @param request 추천을 요청한 그룹 구성원에 대한 정보가 담긴 {@link GroupRecommendationRequest}
     * @return 추천 보드게임 목록의 상세 썸네일이 포함된 {@link GroupRecommendationResponse}
     */
    GroupRecommendationResponse getGroupRecommendation(long userId, GroupRecommendationRequest request);

    /**
     * 사용자가 과거에 받았던 그룹 추천 기록을 날짜 별로 페이징해 조회함.
     *
     * @param userId 기록 조회를 요청한 사용자 ID
     * @param pageable 그룹 추천 목록의 페이징 정보를 담은 {@link Pageable}
     * @return 날짜별 그룹 추천 기록 리스트를 포함한 {@link PageResponse}
     */
    PageResponse getGroupRecommendationHistory(long userId, Pageable pageable);
}
