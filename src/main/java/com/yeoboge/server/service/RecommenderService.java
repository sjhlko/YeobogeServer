package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;

/**
 * 추천 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface RecommenderService {
    /**
     * 외부 추천 목록 생성 API에서 맞춤 추천 보드게임을 요청한 뒤
     * 해당 목록을 포함해 사용자 홈화면에서 제공할 각 카테고리 별 보드게임 썸네일 목록을 구성해
     * {@link RecommendForSingleResponse}에 담아 반환함.
     *
     * @param userId 조회할 사용자 ID
     * @return 맞춤 추천 목록을 포함해 카테고리 별 보드게임 썸네일 목록이 매핑된 {@link RecommendForSingleResponse}
     */
    RecommendForSingleResponse getSingleRecommendation(Long userId);
}
