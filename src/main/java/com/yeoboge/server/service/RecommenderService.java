package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;

/**
 * 추천 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface RecommenderService {
    /**
     * AI를 통한 외부 추천 목록 생성 API에서 맞춤 추천 보드게임을 포함해
     * 사용자 홈화면에서 제공할 각 카테고리 별 보드게임 썸네일 목록 및 타이틀 등의 메타데이터를
     * {@link RecommendForSingleResponse}에 담아 반환함.
     *
     * @param userId 조회할 사용자 ID
     * @return 카테고리 별 추천 보드게임 목록 관련 메타데이터가 담긴 {@link RecommendForSingleResponse}
     */
    RecommendForSingleResponse getSingleRecommendation(Long userId);

    GroupMembersResponse getGroupMembers(long userId, UserGpsDto gpsDto);
}
