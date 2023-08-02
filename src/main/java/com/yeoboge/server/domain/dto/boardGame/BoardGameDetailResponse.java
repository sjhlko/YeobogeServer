package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;
import lombok.Builder;

import java.util.List;

@Builder
public record BoardGameDetailResponse(
        Long id,
        String name,
        String description,
        String weight,
        Integer playerMin,
        Integer playerMax,
        String imagePath,
        Integer playTime,
        String mechanism,
        String isLocalized,
        List<String> genre,
        List<String> theme

) {
    public static BoardGameDetailResponse of(BoardGame boardGame, List<String> theme, List<String> genre){
        return BoardGameDetailResponse.builder()
                .id(boardGame.getId())
                .name(boardGame.getName())
                .description(boardGame.getDescription())
                .weight(boardGame.getWeight())
                .playerMin(boardGame.getPlayerMin())
                .playerMax(boardGame.getPlayerMax())
                .imagePath(boardGame.getImagePath())
                .playTime(boardGame.getPlayTime())
                .mechanism(boardGame.getMechanism())
                .isLocalized(boardGame.getIsLocalized())
                .genre(genre)
                .theme(theme)
                .build();
    }
}
