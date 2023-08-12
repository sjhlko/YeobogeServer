package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.Rating;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QRating.rating;
import static com.yeoboge.server.domain.entity.immutable.QRecentRatings.recentRatings;

/**
 * {@link RatingRepository} 구현
 */
@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {
    private static final int RECENT_RATING_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Double> getUserRatingGroup(Long userId) {
        return queryFactory.select(rating.rate)
                .from(rating)
                .where(rating.user.id.eq(userId))
                .groupBy(rating.rate)
                .fetch();
    }

    @Override
    public List<BoardGame> getRatingByUserId(Long userId, Double score) {
        BoardGameOrderColumn order = BoardGameOrderColumn.NEW;
        Sort sort = Sort.by(order.getDirection(), order.getColumn());

        return queryFactory.select(boardGame)
                .from(boardGame).join(rating)
                .on(rating.boardGame.id.eq(boardGame.id))
                .where(rating.user.id.eq(userId), rating.rate.eq(score))
                .orderBy(getOrderSpecifier(sort))
                .limit(RECENT_RATING_SIZE)
                .fetch();
    }

    @Override
    public Page<BoardGameThumbnailDto> getRatingsByUserId(Long userId, Double score, Pageable pageable) {
        List<BoardGameThumbnailDto> content = getRatingList(userId, score, pageable);
        JPAQuery<Long> countQuery = getCountQuery(userId);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * 페이징을 적용하여 사용자가 {@code score}의 점수로
     * 평가한 보드게임의 {@link BoardGameThumbnailDto} 리스트를 반환함.
     *
     * @param userId 조회할 회원 ID
     * @param score 조회 기준인 평가 점수
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link BoardGameThumbnailDto} 리스트
     */
    private List<BoardGameThumbnailDto> getRatingList(Long userId, Double score, Pageable pageable) {
        JPAQuery<BoardGameThumbnailDto> queryBase = PagingUtils.isSortPopular(pageable.getSort())
                ? getPopularQuery() : getCommonQuery();

        return queryBase.where(rating.user.id.eq(userId), rating.rate.eq(score))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();
    }

    /**
     * 해당 회원이 평가한 보드게임 전 수를 조회할 쿼리를 반환함.
     *
     * @param userId 조회할 회원 ID
     * @return 평가한 보드게임 수를 조회하는 {@link JPAQuery}
     */
    private JPAQuery<Long> getCountQuery(Long userId) {
        return queryFactory.select(boardGame.id.count())
                .from(boardGame)
                .join(rating)
                .on(rating.boardGame.id.eq(boardGame.id))
                .where(rating.user.id.eq(userId));
    }

    /**
     * 정렬 기준이 {@code POPULAR}일 때,
     * 보드게임과 평점, 최근 평점 수 테이블을 조인하는 쿼리를 반환함.
     *
     * @return {@code join}까지 정의된 {@link JPAQuery}
     */
    private JPAQuery<BoardGameThumbnailDto> getPopularQuery() {
        return queryFactory.select(Projections.constructor(BoardGameThumbnailDto.class,
                        boardGame.id,
                        boardGame.name,
                        boardGame.imagePath
                )).from(boardGame)
                .join(rating)
                .on(rating.boardGame.id.eq(boardGame.id))
                .join(recentRatings)
                .on(recentRatings.boardGameId.eq(boardGame.id));
    }

    /**
     * 정렬 기준이 {@code NEW}, {@code EASY}, {@code HARD}일 때,
     * 보드게임과 평점 테이블만 조인하는 쿼리를 반환함.
     *
     * @return {@code join}까지 정의된 {@link JPAQuery}
     */
    private JPAQuery<BoardGameThumbnailDto> getCommonQuery() {
        return queryFactory.select(Projections.constructor(BoardGameThumbnailDto.class,
                        boardGame.id,
                        boardGame.name,
                        boardGame.imagePath
                )).from(boardGame)
                .join(rating)
                .on(rating.boardGame.id.eq(boardGame.id));
    }

    /**
     * {@link Sort}에 정의된 정렬 기준에 따라 {@code QueryDsl}의 정렬 기준인 {@OrderSpecifier}를 반환함.
     * @param sort 조회 시 정렬 기준 {@link Sort}
     * @return {@link OrderSpecifier}
     */
    private OrderSpecifier getOrderSpecifier(Sort sort) {
        return PagingUtils.getBoardGameOrderSpecifier(sort, rating, Rating.class);
    }
}
