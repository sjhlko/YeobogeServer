package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.entity.BookmarkedBoardGame;
import com.yeoboge.server.repository.CustomBookmarkRepository;
import com.yeoboge.server.helper.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QBoardGame.boardGame;
import static com.yeoboge.server.domain.entity.QBookmarkedBoardGame.bookmarkedBoardGame;
import static com.yeoboge.server.domain.entity.immutable.QRecentRatings.recentRatings;

/**
 * {@link CustomBookmarkRepository} 구현
 */
@Repository
@RequiredArgsConstructor
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardGameThumbnailDto> getBookmarkByUserId(Long userId, Pageable pageable) {
        List<BoardGameThumbnailDto> content = getBookmarkList(userId, pageable);
        JPAQuery<Long> countQuery = getCountQuery(userId);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * 페이징을 적용하여 사용자가 북마크한 보드게임의 {@link BoardGameThumbnailDto} 리스트를 반환함.
     *
     * @param userId 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link BoardGameThumbnailDto} 리스트
     */
    private List<BoardGameThumbnailDto> getBookmarkList(Long userId, Pageable pageable) {
        OrderSpecifier specifier = PagingUtils.getBoardGameOrderSpecifier(
                pageable.getSort(), bookmarkedBoardGame, BookmarkedBoardGame.class
        );

        JPAQuery<BoardGameThumbnailDto> queryBase = PagingUtils.isSortPopular(pageable.getSort())
                ? getPopularQuery() : getCommonQuery();

        return queryBase.where(bookmarkedBoardGame.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(specifier)
                .fetch();
    }

    /**
     * 해당 회원의 북마크한 전체 보드게임 수를 조회할 쿼리를 반환함.
     *
     * @param userId 조회할 회원 ID
     * @return 북마크한 보드게임 수를 조회하는 {@link JPAQuery}
     */
    private JPAQuery<Long> getCountQuery(Long userId) {
        return queryFactory.select(boardGame.id.count())
                .from(boardGame)
                .join(bookmarkedBoardGame)
                .on(bookmarkedBoardGame.boardGame.id.eq(boardGame.id))
                .where(bookmarkedBoardGame.user.id.eq(userId));
    }

    /**
     * 정렬 기준이 {@code POPULAR}일 때,
     * 보드게임과 북마, 최근 평점 수 테이블을 조인하는 쿼리를 반환함.
     *
     * @return {@code join}까지 정의된 {@link JPAQuery}
     */
    private JPAQuery<BoardGameThumbnailDto> getPopularQuery() {
        return queryFactory.select(Projections.constructor(BoardGameThumbnailDto.class,
                        boardGame.id,
                        boardGame.name,
                        boardGame.imagePath
                )).from(boardGame)
                .join(bookmarkedBoardGame)
                .on(bookmarkedBoardGame.boardGame.id.eq(boardGame.id))
                .join(recentRatings)
                .on(recentRatings.boardGameId.eq(boardGame.id));
    }

    /**
     * 정렬 기준이 {@code NEW}, {@code EASY}, {@code HARD}일 때,
     * 보드게임과 북마크 테이블만 조인하는 쿼리를 반환함.
     *
     * @return {@code join}까지 정의된 {@link JPAQuery}
     */
    private JPAQuery<BoardGameThumbnailDto> getCommonQuery() {
        return queryFactory.select(Projections.constructor(BoardGameThumbnailDto.class,
                        boardGame.id,
                        boardGame.name,
                        boardGame.imagePath
                )).from(boardGame)
                .join(bookmarkedBoardGame)
                .on(bookmarkedBoardGame.boardGame.id.eq(boardGame.id));
    }
}
