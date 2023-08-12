package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.IsLocalized;
import lombok.Builder;

import java.util.List;

/**
 * 보드게임 단건 조회시 넘겨주는 보드게임의 정보
 *
 * @param id 보드게임의 인덱스
 * @param name 보드게임의 이름
 * @param description 보드게임 설명
 * @param weight 보드게임의 난이도
 * @param playerMin 보드게임을 즐길 수 있는 최소 인원
 * @param playerMax 보드게임을 즐길 수 있는 최대 인원
 * @param imagePath 보드게임 이미지의 경로
 * @param playTime 보드게임 플레이 시간
 * @param mechanism 보드게임의 전략
 * @param isLocalized 보드게임의 번역 여부
 * @param genre 보드게임의 장르의 리스트
 * @param theme 보드게임의 테마의 리스트
 */
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
        IsLocalized isLocalized,
        List<String> mechanism,
        List<String> genre,
        List<String> theme

) {

    /**
     * {@link BoardGame} 엔티티와 테마, 장르 리스트를 통해 {@link BoardGameDetailResponse} DTO 를 생성해 반환함.
     *
     * @param boardGame 정보를 반환할 보드게임의 {@link BoardGame} 엔티티
     * @param theme 정보를 반환할 보드게임의 테마의 리스트
     * @param genre 정보를 반환할 보드게임의 장르의 리스트
     * @return {@link BoardGameDetailResponse} DTO
     */
    public static BoardGameDetailResponse of(BoardGame boardGame, List<String> theme,
                                             List<String> genre, List<String> mechanism){
        return BoardGameDetailResponse.builder()
                .id(boardGame.getId())
                .name(boardGame.getName())
                .description(boardGame.getDescription())
                .weight(boardGame.getWeight())
                .playerMin(boardGame.getPlayerMin())
                .playerMax(boardGame.getPlayerMax())
                .imagePath(boardGame.getImagePath())
                .playTime(boardGame.getPlayTime())
                .isLocalized(boardGame.getIsLocalized())
                .mechanism(mechanism)
                .genre(genre)
                .theme(theme)
                .build();
    }
}
