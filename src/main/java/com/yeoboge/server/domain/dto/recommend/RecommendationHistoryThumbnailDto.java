package com.yeoboge.server.domain.dto.recommend;

import java.util.List;

/**
 * 날짜별 그룹 추천 기록 조회 시 필요한 정보를 담을 DTO
 *
 * @param id 추천 기록 ID
 * @param memberProfileImages 그룹원들의 프로필 이미지 URL 리스트
 * @param recommendedAt 추천을 받았던 시각의 String format
 * @param isMemberMoreThanLimit 당시 추천을 받았던 그룹원 수가 화면에 보여줄 최대 수보다 큰 지에 대한 {@code boolean} 값
 */
public record RecommendationHistoryThumbnailDto(
        long id,
        List<String> memberProfileImages,
        String recommendedAt,
        boolean isMemberMoreThanLimit
) {
}
