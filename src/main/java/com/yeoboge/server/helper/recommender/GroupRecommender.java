package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;

/**
 * 그룹 추천 시 추천할 보드게임 목록을 생성하는 로직과
 * {@link GroupRecommendationResponse}에 해당 데이터를 추가하는 로직을
 * 추상화하기 위한 인터페이스
 */
public interface GroupRecommender {
    void addRecommendationsToResponse(GroupRecommendationResponse response);
}
