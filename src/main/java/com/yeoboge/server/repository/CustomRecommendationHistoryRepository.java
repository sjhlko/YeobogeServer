package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryThumbnailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomRecommendationHistoryRepository {
    Page<RecommendationHistoryThumbnailDto> getRecommendationHistoryPage(long userId, Pageable pageable);
}
