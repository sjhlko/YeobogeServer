package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;

import java.util.List;

public interface RecommendRepository {
    Long getMyFavoriteGenreId(Long userId);
    List<BoardGameThumbnailDto> getPopularBoardGamesOfFavoriteGenre(Long userId, Long genreId);

    List<BoardGameThumbnailDto> getFavoriteBoardGamesOfFriends(Long userId);

    List<BoardGameThumbnailDto> getMyBookmarkedBoardGames(Long userId);

    List<BoardGameThumbnailDto> getTopTenBoardGames();
}
