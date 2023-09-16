package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.QBookmarkedBoardGame;
import com.yeoboge.server.domain.entity.QFriend;
import com.yeoboge.server.domain.entity.QGenreOfBoardGame;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.yeoboge.server.domain.entity.immutable.QRecentRatings.recentRatings;
import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QRating.rating;

@Repository
@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendRepository {
    private static final int BASE_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public Genre getMyFavoriteGenre(Long userId) {
        QGenreOfBoardGame qGenreOfBoardGame = QGenreOfBoardGame.genreOfBoardGame;

        return queryFactory.select(qGenreOfBoardGame.genre)
                .from(qGenreOfBoardGame)
                .join(rating)
                .on(qGenreOfBoardGame.boardGame.id.eq(rating.boardGame.id))
                .where(rating.user.id.eq(userId), rating.score.goe(4.0))
                .groupBy(qGenreOfBoardGame.genre.id)
                .orderBy(rating.id.avg().desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<BoardGameThumbnailDto> getRecommendedBoardGames(List<Long> ids) {
        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .where(boardGame.id.in(ids))
                .fetch();
    }

    @Override
    public List<BoardGameThumbnailDto> getPopularBoardGamesOfFavoriteGenre(Long genreId) {
        QGenreOfBoardGame qGenreOfBoardGame = QGenreOfBoardGame.genreOfBoardGame;

        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .join(qGenreOfBoardGame)
                .on(boardGame.id.eq(qGenreOfBoardGame.boardGame.id))
                .join(recentRatings)
                .on(boardGame.id.eq(recentRatings.boardGameId))
                .where(qGenreOfBoardGame.genre.id.eq(genreId))
                .orderBy(recentRatings.ratings.desc())
                .limit(BASE_SIZE)
                .fetch();
    }

    @Override
    public List<BoardGameThumbnailDto> getFavoriteBoardGamesOfFriends(Long userId) {
        QFriend qFriend = QFriend.friend;
        List<Long> friendsId = queryFactory.select(qFriend.follower.id)
                .from(qFriend)
                .where(qFriend.owner.id.eq(userId))
                .fetch();
        if (friendsId.isEmpty()) return Collections.emptyList();

        List<BoardGameThumbnailDto> boardGameThumbnail = queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .join(rating)
                .on(boardGame.id.eq(rating.boardGame.id))
                .where(rating.user.id.in(friendsId), rating.score.goe(3.5))
                .orderBy(rating.score.desc())
                .limit(BASE_SIZE)
                .fetch();

        return boardGameThumbnail.size() >= BASE_SIZE ? boardGameThumbnail : Collections.emptyList();
    }

    @Override
    public List<BoardGameThumbnailDto> getMyBookmarkedBoardGames(Long userId) {
        QBookmarkedBoardGame qBookmark = QBookmarkedBoardGame.bookmarkedBoardGame;
        List<Long> randomSelectedBookmark = queryFactory.select(qBookmark.boardGame.id)
                .from(qBookmark)
                .where(qBookmark.user.id.eq(userId))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(BASE_SIZE)
                .fetch();

        return randomSelectedBookmark.size() == BASE_SIZE
                ? queryFactory.select(thumbnailConstructorProjection())
                        .from(boardGame)
                        .where(boardGame.id.in(randomSelectedBookmark))
                        .fetch()
                : Collections.emptyList();
    }

    @Override
    public List<BoardGameThumbnailDto> getTopTenBoardGames() {
        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .join(recentRatings)
                .on(boardGame.id.eq(recentRatings.boardGameId))
                .orderBy(recentRatings.ratings.desc())
                .limit(BASE_SIZE)
                .fetch();
    }

    private ConstructorExpression<BoardGameThumbnailDto> thumbnailConstructorProjection() {
        return Projections.constructor(
                BoardGameThumbnailDto.class,
                boardGame.id,
                boardGame.name,
                boardGame.imagePath
        );
    }
}
