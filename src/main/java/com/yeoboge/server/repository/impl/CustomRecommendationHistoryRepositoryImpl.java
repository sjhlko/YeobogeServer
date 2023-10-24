package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryTempDto;
import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryThumbnailDto;
import com.yeoboge.server.repository.CustomRecommendationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QRecommendationHistory.recommendationHistory;
import static com.yeoboge.server.domain.entity.QUser.user;

/**
 * {@link CustomRecommendationHistoryRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class CustomRecommendationHistoryRepositoryImpl
        implements CustomRecommendationHistoryRepository {
    private static final StringExpression DATE_FORMAT = Expressions.stringTemplate(
            "date_format({0}, '%Y-%m-%d %H:%i')",
            recommendationHistory.createdAt
    );
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RecommendationHistoryThumbnailDto> getRecommendationHistoryPage(long userId, Pageable pageable) {
        List<RecommendationHistoryThumbnailDto> content =getHistoryPageContent(userId);
        JPAQuery<Long> countQuery = getBaseQuery(DATE_FORMAT.count(), userId);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * 사용자의 과거 그룹 추천 기록을 조회해
     * 그룹원들의 프로필 이미지와 함께 {@link RecommendationHistoryThumbnailDto}에 담은
     * 리스트를 반환함.
     *
     * @param userId 조회할 사용자 ID
     * @return 그룹 추천 기록에 대한 {@link RecommendationHistoryThumbnailDto} 리스트
     */
    private List<RecommendationHistoryThumbnailDto> getHistoryPageContent(long userId) {
        List<RecommendationHistoryTempDto> tempContent = getBaseQuery(
                Projections.constructor(
                        RecommendationHistoryTempDto.class,
                        recommendationHistory.groupMember,
                        DATE_FORMAT
                ),
                userId
        ).orderBy(DATE_FORMAT.desc()).fetch();

        return tempContent.stream()
                .map(tempDto -> getHistoryThumbnail(tempDto)).toList();
    }

    /**
     * {@link RecommendationHistoryTempDto}로부터 그룹원들의 프로필 이미지를
     * 파싱해 {@link RecommendationHistoryThumbnailDto}로 변환해 반환함.
     *
     * @param tempDto {@link RecommendationHistoryTempDto}
     * @return 특정 timestamp의 그룹 추천 기록에 대한 정보가 담긴 {@link RecommendationHistoryThumbnailDto}
     */
    private RecommendationHistoryThumbnailDto getHistoryThumbnail(RecommendationHistoryTempDto tempDto) {
        final int MAX_GROUP_MEMBER = 3;
        List<Long> groupMemberIds = tempDto.parseGroupMemberIdList();
        List<Long> groupMemberForThumbnail = groupMemberIds.subList(0, MAX_GROUP_MEMBER);
        List<String> profileImages = queryFactory.select(user.profileImagePath)
                .from(user)
                .where(user.id.in(groupMemberForThumbnail))
                .fetch();

        return new RecommendationHistoryThumbnailDto(
                profileImages,
                tempDto.createdAt(),
                groupMemberIds.size() > MAX_GROUP_MEMBER
        );
    }

    /**
     * 그룹 추천 기록을 조회하는 QueryDsl 쿼리의 공통 부분을 반환함.
     *
     * @param select 조회할 컬럼을 매핑하는 {@link Expression}
     * @param userId 조회할 사용자 ID
     * @return {@code <T>} 타입을 조회하는 base 쿼리
     * @param <T> 쿼리 조회 시 조회 결과에 매핑될 DTO 클래스 타입
     */
    private <T> JPAQuery<T> getBaseQuery(Expression<T> select, long userId) {
        return queryFactory.select(select)
                .from(recommendationHistory).distinct()
                .where(recommendationHistory.user.id.eq(userId));
    }
}
