package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.repository.customRepository.BoardGameCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QGenreOfBoardGame.genreOfBoardGame;

/**
 * {@link BoardGameCustomRepository} 구현
 */
@Repository
public class BoardGameCustomRepositoryImpl extends QuerydslRepositorySupport implements BoardGameCustomRepository {

    private final JPAQueryFactory queryFactory;
    public BoardGameCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        super(BoardGame.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 페이징을 적용하여 보드게임 검색 결과를 리턴함
     *
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @param request 검색 조건이 담긴 {@link SearchBoardGameRequest} VO
     * @return {@link BoardGame} 리스트
     */
    @Override
    public Page<BoardGame> findBoardGameBySearchOption(
            Pageable pageable,
            SearchBoardGameRequest request
    ) {
        JPAQuery<BoardGame> query = queryFactory
                .selectFrom(boardGame)
                .join(genreOfBoardGame)
                .on(genreOfBoardGame.boardGame.id.eq(boardGame.id))
                .where(checkGenre(request))
                .where(checkNumOfPlayer(request))
                .where(boardGame.name.contains(request.searchWord()));
        long count = query.fetch().size();
        List<BoardGame> boardGames = Objects.requireNonNull(this.getQuerydsl())
                .applyPagination(pageable,query).fetch();
        return new PageImpl<>(boardGames, pageable, count);
    }

    private BooleanExpression checkNumOfPlayer(SearchBoardGameRequest request){
        if(request.player()==0)
            return null;
        return boardGame.playerMin.gt(request.player()-1);
    }

    private BooleanExpression checkGenre(SearchBoardGameRequest request){
        if(request.genre().size()==0)
            return null;
        return genreOfBoardGame.genre.id.in(request.genre());
    }

}
