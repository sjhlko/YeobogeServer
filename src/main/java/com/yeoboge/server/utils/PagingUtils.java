package com.yeoboge.server.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.QBoardGame;
import com.yeoboge.server.domain.entity.immutable.QRecentRatings;
import com.yeoboge.server.domain.entity.immutable.RecentRatings;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import org.springframework.data.domain.Sort;

public class PagingUtils {
    public static OrderSpecifier getBoardGameOrderSpecifier(Sort sort, Path parent, Class parentClass) {
        Sort.Order order = sort.toList().get(0);
        Order direction = order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;
        String orderProperty = order.getProperty();
        Path fieldPath = null;

        if (orderProperty.equals(BoardGameOrderColumn.NEW.getColumn())) {
            fieldPath = Expressions.path(parentClass, parent, orderProperty);
        } else if (orderProperty.equals(BoardGameOrderColumn.EASY.getColumn())
                || orderProperty.equals(BoardGameOrderColumn.HARD.getColumn())) {
            fieldPath = Expressions.path(BoardGame.class, QBoardGame.boardGame, orderProperty);
        } else if (orderProperty.equals(BoardGameOrderColumn.POPULAR.getColumn())) {
            fieldPath = Expressions.path(RecentRatings.class, QRecentRatings.recentRatings, orderProperty);
        }

        return new OrderSpecifier(direction, fieldPath);
    }

    public static boolean isSortPopular(Sort sort) {
        return sort.toList().get(0).getProperty().equals(BoardGameOrderColumn.POPULAR.getColumn());
    }
}
