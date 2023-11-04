package com.yeoboge.server.domain.dto.boardGame;

import lombok.Builder;

/**
 * 보드게임 상세 조회시 넘겨주는 보드게임의 상세 정보 및
 * 사용자의 평점, 북마크 여부와 친구들의 평가를 담는 DTO Response
 */
@Builder
public record BoardGameDetailResponse(
        BoardGameDetailDto boardGame,
        BookmarkRatingOfUserDto my,
        FriendReviewDto like,
        FriendReviewDto dislike
) { }
