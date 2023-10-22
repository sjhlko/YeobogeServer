package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.*;
import com.yeoboge.server.domain.entity.embeddable.QRecommendationHistory;
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
    private static final int FAV_GENRE_SIZE = 3;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Genre> getMyFavoriteGenre(Long userId) {
        List<Genre> favoriteGenres = queryFactory.select(genreOfBoardGame.genre)
                .from(genreOfBoardGame)
                .join(rating)
                .on(genreOfBoardGame.boardGame.id.eq(rating.boardGame.id))
                .where(rating.user.id.eq(userId), rating.score.goe(3.5))
                .groupBy(genreOfBoardGame.genre.id)
                .orderBy(rating.id.avg().desc())
                .limit(FAV_GENRE_SIZE)
                .fetch();

        if (favoriteGenres.size() < FAV_GENRE_SIZE)
            addUserSelectedFavoriteGenres(favoriteGenres, userId);

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
    public List<BoardGameThumbnailDto> getPopularBoardGamesOfGenreForIndividual(long genreId) {
        return getGenrePopularBoardGameQuery(thumbnailConstructorProjection(), genreId)
                .fetch();
    }

    @Override
    public List<BoardGameDetailedThumbnailDto> getPopularBoardGamesOfGenreForGroup(long genreId) {
        return getGenrePopularBoardGameQuery(boardGame, genreId)
                .fetch()
                .stream().map(BoardGameDetailedThumbnailDto::of)
                .toList();
    }

    @Override
    public List<BoardGameThumbnailDto> getFavoriteBoardGamesOfFriends(Long userId) {
        List<Long> friendsId = getMyFriendIds(userId);
        if (friendsId.isEmpty()) return Collections.emptyList();

        List<BoardGameThumbnailDto> boardGameThumbnail = queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .distinct()
                .join(rating)
                .on(boardGame.id.eq(rating.boardGame.id))
                .where(rating.user.id.in(friendsId).and(rating.score.goe(3.5)))
                .orderBy(rating.score.desc())
                .limit(BASE_SIZE)
                .fetch();

        return boardGameThumbnail.size() >= BASE_SIZE ? boardGameThumbnail : Collections.emptyList();
    }

    @Override
    public List<BoardGameThumbnailDto> getMyBookmarkedBoardGames(Long userId) {
        List<Long> randomSelectedBookmark = getMyBookmarkedBoardGameIds(userId);
        if (randomSelectedBookmark.size() < BASE_SIZE) return Collections.emptyList();

        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .where(boardGame.id.in(randomSelectedBookmark))
                .fetch();
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

    @Override
    public List<BoardGameThumbnailDto> getRecommendationHistories(long userId) {
        QRecommendationHistory qRecommendationHistory = QRecommendationHistory.recommendationHistory;
        return queryFactory.select(thumbnailConstructorProjection())
                .from(boardGame)
                .join(qRecommendationHistory)
                .on(boardGame.id.eq(qRecommendationHistory.boardGameId))
                .where(qRecommendationHistory.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<BoardGameDetailedThumbnailDto> getRecommendationHistoriesWithDetail(long userId) {
        QRecommendationHistory qRecommendationHistory = QRecommendationHistory.recommendationHistory;
        return queryFactory.select(boardGame)
                .from(boardGame)
                .join(qRecommendationHistory)
                .on(boardGame.id.eq(qRecommendationHistory.boardGameId))
                .where(qRecommendationHistory.userId.eq(userId))
                .fetch()
                .stream().map(BoardGameDetailedThumbnailDto::of)
                .toList();
    }

    /**
     * 사용자와 친구인 사용자들의 ID 리스트를 반환함.
     *
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자와 친구인 사용자들의 ID 리스트
     */
    private List<Long> getMyFriendIds(long userId) {
        QFriend qFriend = QFriend.friend;
        return queryFactory.select(qFriend.follower.id)
                .from(qFriend)
                .where(qFriend.owner.id.eq(userId))
                .fetch();
    }

    /**
     * 사용자가 회원 가입 시 선택한 선호 장르를 현재 선호 장르 목록에 추가함.
     *
     * @param favoriteGenres 사용자가 현재 선호하는 장르
     * @param userId 사용자 ID
     */
    private void addUserSelectedFavoriteGenres(List<Genre> favoriteGenres, long userId) {
        QFavoriteGenre qFavoriteGenre = QFavoriteGenre.favoriteGenre;
        List<Long> includedGenreIds = favoriteGenres.stream()
                .map(Genre::getId)
                .toList();

        List<Genre> userSelectedFavGenre = queryFactory.select(qFavoriteGenre.genre)
                .from(qFavoriteGenre)
                .where(qFavoriteGenre.user.id.eq(userId)
                        .and(qFavoriteGenre.genre.id.notIn(includedGenreIds)))
                .orderBy(getRandomOrder())
                .limit(FAV_GENRE_SIZE - favoriteGenres.size())
                .fetch();

        favoriteGenres.addAll(userSelectedFavGenre);
    }

    /**
     * 사용자가 찜한 보드게임목록에서 랜덤으로 추천 목록 수만큼 조회해
     * 해당 보드게임의 ID 리스트를 반환함.
     *
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 찜한 보드게임 중 임의로 선택된 10개의 보드게임 ID 리스트
     */
    private List<Long> getMyBookmarkedBoardGameIds(long userId) {
        QBookmarkedBoardGame qBookmark = QBookmarkedBoardGame.bookmarkedBoardGame;
        return queryFactory.select(qBookmark.boardGame.id)
                .from(qBookmark)
                .where(qBookmark.user.id.eq(userId))
                .orderBy(getRandomOrder())
                .limit(BASE_SIZE)
                .fetch();
    }

    /**
     * 선호하는 장르의 인기 보드게임을 조회하는 쿼리를 반환함.
     *
     * @param select 조회할 엔티티의 컬럼이 지정된 {@link Expression}
     * @param genreId 선호하는 장르 ID
     * @return 해당 장르의 인기 보드게임 목록을 조회하는 {@link JPAQuery}
     * @param <T> 데이터 조회 시 결과를 매핑할 DTO 클래스 타입
     */
    private <T> JPAQuery<T> getGenrePopularBoardGameQuery(Expression<T> select, long genreId) {
        return queryFactory.select(select)
                .from(boardGame)
                .distinct()
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
