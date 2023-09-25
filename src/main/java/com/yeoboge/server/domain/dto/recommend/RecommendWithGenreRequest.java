package com.yeoboge.server.domain.dto.recommend;

/**
 * Spring WebClient를 통해 추천 목록을 생성하는 AI API로
 * POST 요청을 보낼 때 {@code Request Body}에 값을 담기 위한 DTO
 *
 * @param user_id 추천 목록을 생성받을 사용자 ID
 * @param genre_id 추천 목록에 포함될 보드게임의 장르 ID
 */
public record RecommendWithGenreRequest(long user_id, long genre_id) {
}
