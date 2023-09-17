package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.Genre;

import java.util.List;

public interface RecommendRepository {
    List<Genre> getMyFavoriteGenre(Long userId);
    List<BoardGameThumbnailDto> getRecommendedBoardGames(List<Long> ids);
    List<BoardGameThumbnailDto> getPopularBoardGamesOfFavoriteGenre(Long genreId);

    List<BoardGameThumbnailDto> getFavoriteBoardGamesOfFriends(Long userId);

    List<BoardGameThumbnailDto> getMyBookmarkedBoardGames(Long userId);

    List<BoardGameThumbnailDto> getTopTenBoardGames();
}
