package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;

import java.util.concurrent.CountDownLatch;

public interface RecommendedBySomething {
    void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch);
}
