package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryThumbnailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 그룹 추천 기록에 관한 QueryDsl 쿼리 메서드를 정의하는 인터페이스
 */
public interface CustomRecommendationHistoryRepository {
    /**
     * 사용자의 과거 그룹 추천 기록에 대한 {@link RecommendationHistoryThumbnailDto} 리스트를 페이징해 조회함.
     *
     * @param userId 조회를 요청한 사용자 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 페이징된 날짜별 그룹 추천 기록 {@link RecommendationHistoryThumbnailDto} 리스트
     */
    Page<RecommendationHistoryThumbnailDto> getRecommendationHistoryPage(long userId, Pageable pageable);
}
