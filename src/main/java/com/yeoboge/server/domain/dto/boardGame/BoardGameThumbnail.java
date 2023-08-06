package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;
import lombok.Builder;

/**
 * 보드게임 목록 조회 시 각 보드게임의 기본 정보만 담는 DTO
 *
 * @param id 보드게임 ID
 * @param name 보드게임 명
 * @param imagePath 보드게임 썸네일 이미지 URL
 */
@Builder
public record BoardGameThumbnail(
        Long id,
        String name,
        String imagePath
) {

    /**
     * {@link BoardGame} 엔티티로부터 필요한 정보를 가져옴.
     *
     * @param boardGame 원본 보드게임
     * @return 해당 보드게임의 정보가 담긴 {@link BoardGameThumbnail}
     */
    public static BoardGameThumbnail of(BoardGame boardGame) {
        return BoardGameThumbnail.builder()
                .id(boardGame.getId())
                .name(boardGame.getName())
                .imagePath(boardGame.getImagePath())
                .build();
    }
}
