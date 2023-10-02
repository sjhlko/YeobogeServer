package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;

import java.util.concurrent.CountDownLatch;

/**
 * 개인 추천 목록 생성 시 각 카테고리 별 목록을 생성하는 로직과
 * {@link RecommendForSingleResponse}에 해당 데이터를 추가하는 로직을
 * 추상화하기 위한 인터페이스
 */
public interface RecommendedBySomething {
    /**
     * 각 카테고리 별 자체 로직으로 생성한 추천 보드게임 목록 및 타이틀 등을
     * {@link RecommendForSingleResponse}에 추가함.
     * 또한 비동기로 동작하는 로직이 완료되었을 때 Servlet 기반 서버 어플리케이션이
     * 이를 대기할 때 사용되는 {@link CountDownLatch} 값을 감소시킴.
     *
     * @param response {@link RecommendForSingleResponse}
     * @param latch 비동기 작업의 완료될 때까지 대기하기 위한 {@link CountDownLatch}
     */
    void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch);
}
