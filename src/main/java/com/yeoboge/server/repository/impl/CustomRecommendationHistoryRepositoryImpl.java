package com.yeoboge.server.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryTempDto;
import com.yeoboge.server.domain.dto.recommend.RecommendationHistoryThumbnailDto;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.CustomRecommendationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QRecommendationHistory.recommendationHistory;
import static com.yeoboge.server.domain.entity.QUser.user;

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

    private RecommendationHistoryThumbnailDto getHistoryThumbnail(RecommendationHistoryTempDto tempDto) {
        final int MAX_GROUP_MEMBER = 3;
        List<Long> groupMemberIds = parseGroupMemberIdList(tempDto.groupMember());
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

    private List<Long> parseGroupMemberIdList(String groupMembers) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(groupMembers, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> JPAQuery<T> getBaseQuery(Expression<T> select, long userId) {
        return queryFactory.select(select)
                .from(recommendationHistory).distinct()
                .where(recommendationHistory.user.id.eq(userId));
    }
}
