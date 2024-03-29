package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.repository.CustomFriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QUser.user;
import static com.yeoboge.server.domain.entity.QFriend.friend;

/**
 * {@link CustomFriendRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class CustomFriendRepositoryImpl implements CustomFriendRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserInfoDto> getFriendsPage(Long id, Pageable pageable) {
        List<UserInfoDto> content = getFriendList(id, pageable);
        JPAQuery<Long> countQuery = getFriendCountQuery(id);

        /* count가 필요하지 않을 때 해당 쿼리를 실행하지 않음 */
        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * 해당 회원의 친구 목록을 페이징 크기만큼 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link UserInfoDto} 리스트
     */
    private List<UserInfoDto> getFriendList(Long id, Pageable pageable) {
        return getFriendQueryBase(id,
                Projections.constructor(UserInfoDto.class,
                        user.id,
                        user.nickname,
                        user.profileImagePath.as("imagePath")
                )).orderBy(user.nickname.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 해당 회원의 전체 친구 수를 조회할 쿼리를 반환함.
     *
     * @param id 조회할 회원 ID
     * @return 친구 수를 조회하는 {@link JPAQuery}
     */
    private JPAQuery<Long> getFriendCountQuery(Long id) {
        return getFriendQueryBase(id, user.count());
    }

    /**
     * 회원의 친구 조회 시 조회 테이블, 조인 조건 등이 지정된 쿼리를 반환함.
     *
     * @param id 조회할 회원 ID
     * @param selectExpression {@select} 쿼리에서 조회할 컬럼이 지정된 {@link Expression}
     * @return {@code select} 조회 결과
     * @param <T> 쿼리 조회 후 반환할 클래스 타입
     */
    private <T> JPAQuery<T> getFriendQueryBase(Long id, Expression<T> selectExpression) {
        return queryFactory.select(selectExpression)
                .from(user)
                .join(friend)
                .on(friend.follower.id.eq(user.id))
                .where(friend.owner.id.eq(id));
    }
}
