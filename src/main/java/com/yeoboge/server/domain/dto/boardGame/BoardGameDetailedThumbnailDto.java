package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.GenreOfBoardGame;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * 보드게임 검색 결과 조회 시 조회 될 각 보드게임의 정보를 담는 DTO
 *
 * @param id BGG 기준의 보드게임 id
 * @param name 보드게임의 이름
 * @param imagePath 보드게임의 이미지 링크
 * @param playerMax 보드게임의 플레이 가능한 최대 인원수
 * @param playerMin 보드게임의 플레이 가능한 최소 인원수
 * @param weight 보드게임의 난이도
 * @param weight 보드게임의 장르
 */
@Builder
public record BoardGameDetailedThumbnailDto(
        Long id,
        String name,
        String imagePath,
        Integer playerMin,
        Integer playerMax,
        String weight,
        List<String> genre

) {

    /**
     * 특정 보드게임의 entity로 보드게임 검색 결과 조회 시 조회 될 각 보드게임의 정보를 담는 DTO를 생성함
     *
     * @param boardGame 보드게임 검색 결과 DTO를 생성할 보드게임 entity
     */
    public static BoardGameDetailedThumbnailDto of(BoardGame boardGame) {
        return BoardGameDetailedThumbnailDto.builder()
                .id(boardGame.getId())
                .name(boardGame.getName())
                .imagePath(boardGame.getImagePath())
                .playerMax(boardGame.getPlayerMax())
                .playerMin(boardGame.getPlayerMin())
                .genre(getGenreOf(boardGame))
                .weight(boardGame.getWeight())
                .build();
    }

    /**
     * 특정 보드게임의 장르의 이름들을 리스트 형태로 리턴함
     *
     * @param boardGame 장르를 알아낼 보드게임 entity
     */
    public static List<String> getGenreOf(BoardGame boardGame){
        ArrayList<String> genre = new ArrayList<>();
        for (GenreOfBoardGame genreOfBoardGame : boardGame.getGenre()) {
            genre.add(genreOfBoardGame.getGenre().getName());
        }
        return genre;
    }
}
