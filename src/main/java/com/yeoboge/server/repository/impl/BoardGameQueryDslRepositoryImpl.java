package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.BookmarkedBoardGame;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import com.yeoboge.server.repository.BoardGameQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QBookmarkedBoardGame.bookmarkedBoardGame;

/**
 * {@link BoardGameQueryDslRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class BoardGameQueryDslRepositoryImpl implements BoardGameQueryDslRepository {
    private static final int BOARD_GAME_PAGE_SIZE = 20;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> getBookmarkByUserId(Long userId, Integer page, BoardGameOrderColumn order) {
        List<BoardGame> boardGames = queryFactory.select(boardGame).from(boardGame).join(bookmarkedBoardGame)
                .on(bookmarkedBoardGame.boardGame.id.eq(boardGame.id))
                .where(bookmarkedBoardGame.user.id.eq(userId))
                .offset(page * BOARD_GAME_PAGE_SIZE)
                .limit(BOARD_GAME_PAGE_SIZE)
                .orderBy(sortOption(order))
                .fetch();
        return boardGames;
    }

    /**
     * {@link BoardGameOrderColumn} 타입의 정렬 기준을 실제 엔티티의 컬럼과 매핑함.
     *
     * @param order 목록 정렬 기준
     * @return 정렬할 컬럼 및 방향이 정해진 {@link OrderSpecifier}
     */
    private OrderSpecifier sortOption(BoardGameOrderColumn order) {
        Order direction = order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;
        String orderProperty = order.getColumn();
        Path fieldPath;

        switch (order) {
            case NEW -> fieldPath = Expressions.path(BookmarkedBoardGame.class, bookmarkedBoardGame, orderProperty);
            case EASY, HARD -> fieldPath = Expressions.path(BoardGame.class, boardGame, orderProperty);
            default -> fieldPath = null;
        }

        return new OrderSpecifier(direction, fieldPath);
    }
}
