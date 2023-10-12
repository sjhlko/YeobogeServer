package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.Genre;

import java.util.List;

/**
 * 개인 추천 목록 생성과 관련된 SQL 쿼리를 제공하는 인터페이스
 */
public interface RecommendRepository {
    /**
     * 사용자가 선호하는 장르의 ID를 조회함.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 선호하는 장르의 {@link Genre} 엔티티 리스트
     */
    List<Genre> getMyFavoriteGenre(Long userId);

    /**
     * 외부 AI API가 생성한 추천 보드게임 ID 리스트에 대해
     * {@link BoardGameThumbnailDto}에 해당하는 컬럼 데이터를 조회함.
     *
     * @param ids AI가 추천한 보드게임 ID 리스트
     * @return 해당 ID들의 {@link BoardGameThumbnailDto} 리스트
     */
    List<BoardGameThumbnailDto> getRecommendedBoardGamesForIndividual(List<Long> ids);

    List<BoardGameDetailedThumbnailDto> getRecommendedBoardGamesForGroup(List<Long> ids);

    /**
     * 사용자가 선호하는 장르의 인기 보드게임 목록을 조회함.
     *
     * @param genreId 조회할 보드게임 장르 ID
     * @return 해당 장르의 인기 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    List<BoardGameThumbnailDto> getPopularBoardGamesOfFavoriteGenre(Long genreId);

    /**
     * 사용자의 친구들이 좋은 평가를 준 보드게임 목록을 조회함.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자 친구들의 고평가 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    List<BoardGameThumbnailDto> getFavoriteBoardGamesOfFriends(Long userId);

    /**
     * 사용자가 찜한 보드게임의 일부를 랜덤으로 조회함.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 찜한 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    List<BoardGameThumbnailDto> getMyBookmarkedBoardGames(Long userId);

    /**
     * 전 장르 통합 인기 보드게임 목록을 조회함.
     *
     * @return 장르 무관 인기 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    List<BoardGameThumbnailDto> getTopTenBoardGames();
}
