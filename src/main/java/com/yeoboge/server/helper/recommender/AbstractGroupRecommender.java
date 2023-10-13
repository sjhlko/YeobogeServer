package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 그룹 추천 목록 생성 시 그룹 구성원 특성에 따라
 * 달라지는 추천 로직을 분리하기 위한 추상 클래스
 */
@AllArgsConstructor
public abstract class AbstractGroupRecommender implements GroupRecommender {
    protected RecommendRepository repository;
    protected GroupRecommendationRequest request;
    protected List<Long> recommendableMembers;
}
