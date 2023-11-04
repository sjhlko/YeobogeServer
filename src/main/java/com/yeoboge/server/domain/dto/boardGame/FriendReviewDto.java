package com.yeoboge.server.domain.dto.boardGame;

import lombok.Builder;

/**
 * 보드게임 상세 조회 시 해당 보드게임에 대한 친구의 평가를 담는 DTO
 *
 * @param friendId 친구의 ID
 * @param nickname 친구의 닉네임
 * @param imagePath 친구의 프로필 이미지 URL
 * @param rating 친구가 보드게임에 남긴 평점
 */
@Builder
public record FriendReviewDto(long friendId, String nickname, String imagePath, double rating) {
}
