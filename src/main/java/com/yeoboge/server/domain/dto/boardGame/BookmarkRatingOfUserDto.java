package com.yeoboge.server.domain.dto.boardGame;

import lombok.Builder;

/**
 * 특정 보드게임에 대한 사용자의 평점 및 북마크 여부를 담는 DTO
 *
 * @param rating 사용자의 평점
 * @param isBookmarked 사용자의 북마크 여부
 */
@Builder
public record BookmarkRatingOfUserDto(Double rating, boolean isBookmarked) {
}
