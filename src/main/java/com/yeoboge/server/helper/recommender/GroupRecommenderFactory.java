package com.yeoboge.server.helper.recommender;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * 그룹 추천 시 상황에 맞는 {@link GroupRecommender}를 생성하기 위한 팩토리 클래스
 */
public interface GroupRecommenderFactory {
    /**
     * 그룹 상황에 맞는 {@link GroupRecommender}를 반환함.
     *
     * @param client 외부 API 요청시 필요한 {@link WebClient}
     * @return {@link GroupRecommender}
     */
    GroupRecommender getRecommender(WebClient client);
}
