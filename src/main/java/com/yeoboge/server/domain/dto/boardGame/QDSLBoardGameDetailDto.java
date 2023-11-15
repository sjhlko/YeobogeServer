package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.IsLocalized;
import com.yeoboge.server.domain.entity.Weight;

/**
 * 보드게임 상세 조회 시 보드게임 엔티티 자체의 속성 및 평균 평점을
 * 조회하는 QueryDsl 쿼리에서 {@link com.querydsl.core.types.Projections}을 적용하기 위한 DTO
 *
 * @param id 보드게임의 인덱스
 * @param name 보드게임의 이름
 * @param description 보드게임 설명
 * @param weight 보드게임의 난이도
 * @param playerMin 보드게임을 즐길 수 있는 최소 인원
 * @param playerMax 보드게임을 즐길 수 있는 최대 인원
 * @param imagePath 보드게임 이미지의 경로
 * @param playTime 보드게임 플레이 시간
 * @param isLocalized 보드게임의 번역 여부
 * @param avgRating 보드게임 평균 별점
 */
public record QDSLBoardGameDetailDto(
        long id,
        String name,
        String description,
        Weight weight,
        int playerMin,
        int playerMax,
        String imagePath,
        int playTime,
        IsLocalized isLocalized,
        double avgRating
) { }
