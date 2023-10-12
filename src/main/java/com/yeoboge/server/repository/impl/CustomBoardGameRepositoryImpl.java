package com.yeoboge.server.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.repository.CustomBoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QGenreOfBoardGame.genreOfBoardGame;

/**
 * {@link CustomBoardGameRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class CustomBoardGameRepositoryImpl implements CustomBoardGameRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardGameDetailedThumbnailDto> findBoardGameBySearchOption(
            Pageable pageable,
            SearchBoardGameRequest request
    ) {
        List<BoardGameDetailedThumbnailDto> content = getBaseQuery(boardGame)
                .where(whereConditionOnSearching(request))
                .orderBy(boardGame.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(BoardGameDetailedThumbnailDto::of)
                .toList();
        JPAQuery<Long> countQuery = getCountQuery(request);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    private <T> JPAQuery<T> getBaseQuery(Expression<T> selectExpression) {
        return queryFactory.select(selectExpression)
                .from(boardGame)
                .distinct()
                .leftJoin(genreOfBoardGame)
                .on(genreOfBoardGame.boardGame.id.eq(boardGame.id));
    }

    private JPAQuery<Long> getCountQuery(SearchBoardGameRequest request) {
        return getBaseQuery(boardGame.count())
                .where(whereConditionOnSearching(request));
    }

    private Predicate whereConditionOnSearching(SearchBoardGameRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(checkGenre(request.genre()));
        builder.and(checkNumOfPlayer(request.player()));
        builder.and(checkNameKeyword(request.searchWord()));

        return builder.getValue();
    }

    private BooleanExpression checkNumOfPlayer(int numPlayer){
        if (numPlayer == 0) return null;
        return boardGame.playerMin.loe(numPlayer).and(boardGame.playerMax.goe(numPlayer));
    }

    private BooleanExpression checkGenre(List<Long> genreIds){
        if (genreIds == null) return null;
        return genreOfBoardGame.genre.id.in(genreIds);
    }

    private BooleanExpression checkNameKeyword(String keyword) {
        if (keyword == null) return null;
        return boardGame.name.contains(keyword);
    }
}
