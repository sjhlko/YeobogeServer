package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;

public interface GroupRecommender {
    void addRecommendationsToResponse(GroupRecommendationResponse response);
}
