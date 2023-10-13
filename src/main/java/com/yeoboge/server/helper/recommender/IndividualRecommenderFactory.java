package com.yeoboge.server.helper.recommender;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * 개인 추천 시 사용자 특성에 따라 {@link IndividualRecommender} 리스트를 생성하기 위한 팩토리 클래스
 */
public interface IndividualRecommenderFactory {
    /**
     * 사용자에게 추천 목록 생성을 담당할 {@link IndividualRecommender} 리스트를 반환함.
     *
     * @param client 외부 API 요청 시 필요한 {@link WebClient}
     * @return {@link IndividualRecommender}
     */
    List<IndividualRecommender> getRecommenders(WebClient client);
}
