package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;

import java.util.List;

/**
 * 그룹 추천 시 추천된 보드게임 목록에 대한 상세 썸네일 리스트를 담는 DTO
 * @param recommendations 추천된 보드게임 목록의 {@link BoardGameDetailedThumbnailDto} 리스트
 */
public record GroupRecommendationResponse(
        List<BoardGameDetailedThumbnailDto> recommendations
) {
    /**
     * {@link BoardGameDetailedThumbnailDto} 리스트를 내부 멤버 리스트에 추가함.
     *
     * @param boardGames 보드게임 목록의 {@link BoardGameDetailedThumbnailDto} 리스트
     */
    public void addRecommendations(List<BoardGameDetailedThumbnailDto> boardGames) {
        recommendations.addAll(boardGames);
    }
}
