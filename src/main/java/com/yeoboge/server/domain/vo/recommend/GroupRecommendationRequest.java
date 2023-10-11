package com.yeoboge.server.domain.vo.recommend;

import java.util.List;

public record GroupRecommendationRequest(List<Long> members, int seed) {
    public void setRecommendableMembers(List<Long> recommendableMembers) {
        members.clear();
        members.addAll(recommendableMembers);
    }
}
