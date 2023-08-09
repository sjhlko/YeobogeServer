package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.BookmarkedBoardGame;
import com.yeoboge.server.domain.entity.QBookmarkedBoardGame;
import com.yeoboge.server.domain.entity.QRating;
import com.yeoboge.server.domain.entity.immutable.RecentRatings;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import com.yeoboge.server.repository.BoardGameQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QBookmarkedBoardGame.bookmarkedBoardGame;
import static com.yeoboge.server.domain.entity.QRating.rating;
import static com.yeoboge.server.domain.entity.immutable.QRecentRatings.recentRatings;

/**
 * {@link BoardGameQueryDslRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class BoardGameQueryDslRepositoryImpl implements BoardGameQueryDslRepository {
    private static final int BOARD_GAME_PAGE_SIZE = 20;
    private static final int RECENT_RATING_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> getBookmarkByUserId(Long userId, Integer page, BoardGameOrderColumn order) {
        return getPathBaseByOrderColumn(order, bookmarkedBoardGame, bookmarkedBoardGame.boardGame.id)
                .where(bookmarkedBoardGame.user.id.eq(userId))
                .offset(page * BOARD_GAME_PAGE_SIZE)
                .limit(BOARD_GAME_PAGE_SIZE)
                .orderBy(sortOption(order, bookmarkedBoardGame))
                .fetch();
    }

    @Override
    public List<Double> getUserRatingGroup(Long userId) {
        return queryFactory.select(rating.rate)
                .from(rating)
                .where(rating.user.id.eq(userId))
                .groupBy(rating.rate)
                .fetch();
    }

    @Override
    public List<BoardGame> getRatingByUserId(Long userId, Double rate) {
        return queryFactory.select(boardGame)
                .from(boardGame).join(rating)
                .on(rating.boardGame.id.eq(boardGame.id))
                .where(rating.user.id.eq(userId), rating.rate.eq(rate))
                .orderBy(sortOption(BoardGameOrderColumn.NEW, rating))
                .limit(RECENT_RATING_SIZE)
                .fetch();
    }

    @Override
    public List<BoardGame> getRatingsByUserId(Long userId, Double score, Integer page, BoardGameOrderColumn order) {
        return getPathBaseByOrderColumn(order, rating, rating.boardGame.id)
                .where(rating.user.id.eq(userId), rating.rate.eq(score))
                .offset(page * BOARD_GAME_PAGE_SIZE)
                .limit(BOARD_GAME_PAGE_SIZE)
                .orderBy(sortOption(order, rating))
                .fetch();
    }

    private JPAQueryBase<BoardGame, ?> getPathBaseByOrderColumn(BoardGameOrderColumn order,
                                                                EntityPathBase joinPath,
                                                                NumberPath joinColumn) {
        if (!(joinPath instanceof QRating) && !(joinPath instanceof QBookmarkedBoardGame))
            throw new IllegalArgumentException();

        if (order == BoardGameOrderColumn.POPULAR) {
            return queryFactory.select(boardGame)
                    .from(boardGame)
                    .join(joinPath).on(joinColumn.eq(boardGame.id))
                    .join(recentRatings).on(recentRatings.boardGameId.eq(boardGame.id));
        }

        return queryFactory.select(boardGame)
                .from(boardGame)
                .join(joinPath).on(joinColumn.eq(boardGame.id));
    }

    /**
     * {@link BoardGameOrderColumn} 타입의 정렬 기준을 실제 엔티티의 컬럼과 매핑함.
     *
     * @param order 목록 정렬 기준
     * @return 정렬할 컬럼 및 방향이 정해진 {@link OrderSpecifier}
     */
    private OrderSpecifier sortOption(BoardGameOrderColumn order, EntityPathBase newPath) {
        Order direction = order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;
        String orderProperty = order.getColumn();
        Path fieldPath;

        switch (order) {
            case NEW -> fieldPath = Expressions.path(BookmarkedBoardGame.class, newPath, orderProperty);
            case EASY, HARD -> fieldPath = Expressions.path(BoardGame.class, boardGame, orderProperty);
            case POPULAR ->  fieldPath = Expressions.path(RecentRatings.class, recentRatings, orderProperty);
            default -> fieldPath = null;
        }

        return new OrderSpecifier(direction, fieldPath);
    }
}
