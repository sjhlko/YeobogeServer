package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.*;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.yeoboge.server.domain.entity.QGenreOfBoardGame.genreOfBoardGame;
import static com.yeoboge.server.domain.entity.immutable.QRecentRatings.recentRatings;
import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QRating.rating;

/**
 * {@link RecommendRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendRepository {
    private static final int BASE_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Genre> getMyFavoriteGenre(Long userId) {
        final int FAV_GENRE_SIZE = 3;
        QFavoriteGenre qFavoriteGenre = QFavoriteGenre.favoriteGenre;
        List<Genre> favoriteGenres = queryFactory.select(genreOfBoardGame.genre)
                .from(genreOfBoardGame)
                .join(rating)
                .on(genreOfBoardGame.boardGame.id.eq(rating.boardGame.id))
                .where(rating.user.id.eq(userId), rating.score.goe(3.5))
                .groupBy(genreOfBoardGame.genre.id)
                .orderBy(rating.id.avg().desc())
                .limit(FAV_GENRE_SIZE)
                .fetch();

        if (favoriteGenres.size() < FAV_GENRE_SIZE) {
            List<Long> includedGenreIds = favoriteGenres.stream()
                    .map(Genre::getId)
                    .toList();
            favoriteGenres.addAll(queryFactory.select(qFavoriteGenre.genre)
                    .from(qFavoriteGenre)
                    .where(
                            qFavoriteGenre.user.id.eq(userId),
                            qFavoriteGenre.genre.id.notIn(includedGenreIds)
                    ).orderBy(getRandomOrder())
                    .limit(FAV_GENRE_SIZE - favoriteGenres.size())
                    .fetch()
            );
        }

        return favoriteGenres;
    }

    @Override
    public List<BoardGameThumbnailDto> getRecommendedBoardGamesForIndividual(List<Long> ids) {
        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .where(boardGame.id.in(ids))
                .fetch();
    }

    @Override
    public List<BoardGameDetailedThumbnailDto> getRecommendedBoardGamesForGroup(List<Long> ids) {
        return queryFactory.select(boardGame)
                .from(boardGame)
                .leftJoin(genreOfBoardGame)
                .on(genreOfBoardGame.boardGame.id.eq(boardGame.id))
                .where(boardGame.id.in(ids))
                .fetch()
                .stream().map(BoardGameDetailedThumbnailDto::of)
                .toList();
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
                .orderBy(getRandomOrder())
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

    private <T> JPAQuery<T> getGenrePopularBoardGameQuery(Expression<T> select, long genreId) {
        return queryFactory.select(select)
                .from(boardGame)
                .join(genreOfBoardGame)
                .on(boardGame.id.eq(genreOfBoardGame.boardGame.id))
                .join(recentRatings)
                .on(boardGame.id.eq(recentRatings.boardGameId))
                .where(genreOfBoardGame.genre.id.eq(genreId))
                .orderBy(recentRatings.ratings.desc())
                .limit(BASE_SIZE);
    }

    /**
     * 전체 조회 쿼리가 공통으로 사용하는 {@link BoardGameThumbnailDto} DTO 매핑 {@code Expression}을 반환함.
     *
     * @return DTO 매핑에 대한 {@link ConstructorExpression}
     */
    private ConstructorExpression<BoardGameThumbnailDto> thumbnailConstructorProjection() {
        return Projections.constructor(
                BoardGameThumbnailDto.class,
                boardGame.id,
                boardGame.name,
                boardGame.imagePath
        );
    }

    /**
     * DB 조회 시 결과 데이터들을 랜덤으로 가져오기 위해
     * 해당 SQL 함수의 {@link Expressions}에 대한 정렬 {@link OrderSpecifier}를 반환함.
     *
     * @return SQL 랜덤 함수에 대한 {@link OrderSpecifier}
     */
    private OrderSpecifier getRandomOrder() {
        return Expressions.numberTemplate(Double.class, "function('rand')").asc();
    }
}
