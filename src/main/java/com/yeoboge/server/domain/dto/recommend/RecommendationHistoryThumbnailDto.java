package com.yeoboge.server.domain.dto.recommend;

import java.util.List;

public record RecommendationHistoryThumbnailDto(
        List<String> memberProfileImages,
        String recommendedAt,
        boolean isGroupMemberOver
) {
}
