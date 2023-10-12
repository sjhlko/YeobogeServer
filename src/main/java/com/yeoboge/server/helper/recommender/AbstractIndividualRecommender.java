package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;

/**
 * 개인 추천 목록 생성 시 각 카테고리 별로 공통적으로
 * 사용되는 로직을 분리하기 위한 추상 클래스
 */
public abstract class AbstractIndividualRecommender implements Recommender {
    protected RecommendRepository repository;
    protected String key;
    protected String description;

    AbstractIndividualRecommender(RecommendRepository repository, RecommendTypes type) {
        this.repository = repository;
        this.key = type.getKey();
    }
}
