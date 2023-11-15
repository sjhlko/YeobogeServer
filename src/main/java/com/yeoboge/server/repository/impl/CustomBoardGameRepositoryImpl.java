package com.yeoboge.server.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailDto;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.QDSLBoardGameDetailDto;
import com.yeoboge.server.domain.entity.*;
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
import static com.yeoboge.server.domain.entity.QThemeOfBoardGame.themeOfBoardGame;
import static com.yeoboge.server.domain.entity.QMechanismOfBoardGame.mechanismOfBoardGame;

/**
 * {@link CustomBoardGameRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class CustomBoardGameRepositoryImpl implements CustomBoardGameRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public BoardGameDetailDto getBoardGameDetail(long boardGameId) {
        QGenre qGenre = QGenre.genre;
        QTheme qTheme = QTheme.theme;
        QMechanism qMechanism = QMechanism.mechanism;
        QRating qRating = QRating.rating;
        QDSLBoardGameDetailDto detailDto = queryFactory.select(
                Projections.constructor(
                        QDSLBoardGameDetailDto.class,
                        boardGame.id,
                        boardGame.name,
                        boardGame.description,
                        boardGame.weight,
                        boardGame.playerMin,
                        boardGame.playerMax,
                        boardGame.imagePath,
                        boardGame.playTime,
                        boardGame.isLocalized,
                        qRating.score.avg().coalesce(0.0).as("avgRating")
                ))
                .from(boardGame)
                .leftJoin(qRating)
                .on(boardGame.id.eq(qRating.boardGame.id))
                .where(boardGame.id.eq(boardGameId))
                .fetchOne();
        List<String> genres = queryFactory.select(qGenre.name)
                .from(boardGame)
                .join(genreOfBoardGame)
                .on(boardGame.id.eq(genreOfBoardGame.boardGame.id))
                .join(qGenre)
                .on(qGenre.id.eq(genreOfBoardGame.genre.id))
                .where(boardGame.id.eq(boardGameId))
                .fetch();
        List<String> themes = queryFactory.select(qTheme.name)
                .from(boardGame)
                .join(themeOfBoardGame)
                .on(boardGame.id.eq(themeOfBoardGame.boardGame.id))
                .join(qTheme)
                .on(qTheme.id.eq(themeOfBoardGame.theme.id))
                .where(boardGame.id.eq(boardGameId))
                .fetch();
        List<String> mechanisms = queryFactory.select(qMechanism.name)
                .from(boardGame)
                .join(mechanismOfBoardGame)
                .on(boardGame.id.eq(mechanismOfBoardGame.boardGame.id))
                .join(qMechanism)
                .on(qMechanism.id.eq(mechanismOfBoardGame.mechanism.id))
                .where(boardGame.id.eq(boardGameId))
                .fetch();

        return BoardGameDetailDto.of(detailDto, themes, genres, mechanisms);
    }

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
