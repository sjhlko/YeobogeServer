package com.yeoboge.server.domain.vo.recommend;

/**
 * 추천 목록 생성 API에서 응답 시 각 항목의 JSON 형식과 매핑할 VO
 * (임시로 구현, 실제 응답 형식은 달라질 예정)
 *
 * @param id 추천된 보드게임 ID
 * @param score 예상 평점
 */
public record RecommendIds(Long id, Double score) {
}
