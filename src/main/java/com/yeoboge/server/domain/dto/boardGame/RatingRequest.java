package com.yeoboge.server.domain.dto.boardGame;

/**
 * 보드게임 평가 시 클라이언트에서 넘겨주는 평가할 별점
 *
 * @param score 보드게임 별점
 */
public record RatingRequest(Double score) {
}
