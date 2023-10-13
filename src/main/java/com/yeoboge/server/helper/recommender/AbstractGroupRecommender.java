package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class AbstractGroupRecommender implements Recommender {
    protected RecommendRepository repository;
    protected GroupRecommendationRequest request;
    protected List<Long> recommendableMembers;
}
