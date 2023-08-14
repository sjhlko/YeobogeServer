package com.yeoboge.server.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yeoboge.server.domain.entity.QUser.user;
import static com.yeoboge.server.domain.entity.QFriend.friend;
import static com.yeoboge.server.domain.entity.QFriendRequest.friendRequest;

/**
 * {@link FriendRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FriendInfoDto> getFriendsPage(Long id, Pageable pageable) {
        List<FriendInfoDto> content = getFriendList(id, pageable);
        JPAQuery<Long> countQuery = getFriendCountQuery(id);

        /* count가 필요하지 않을 때 해당 쿼리를 실행하지 않음 */
        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Page<FriendInfoDto> getFriendRequestsPage(Long id, Pageable pageable) {
        List<FriendInfoDto> content = getFriendRequestList(id, pageable);
        JPAQuery<Long> countQuery = getRequestCountQuery(id);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * 해당 회원의 친구 목록을 페이징 크기만큼 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link FriendInfoDto} 리스트
     */
    private List<FriendInfoDto> getFriendList(Long id, Pageable pageable) {
        return getFriendQueryBase(id,
                Projections.constructor(FriendInfoDto.class,
                        user.id,
                        user.nickname,
                        user.profileImagePath.as("imagePath")
                )).orderBy(user.nickname.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 해당 회원에게 요청된 친구 요청 목록을 페이징 크기만큼 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link FriendInfoDto} 리스트
     */
    private List<FriendInfoDto> getFriendRequestList(Long id, Pageable pageable) {
        return getRequestQueryBase(id,
                Projections.constructor(FriendInfoDto.class,
                        user.id,
                        user.nickname,
                        user.profileImagePath.as("imagePath")
                )).orderBy(friendRequest.createdAt.desc())
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
     * 해당 회원의 전체 친구 요청 수를 조회할 쿼리를 반환함.
     *
     * @param id 조회할 회원 ID
     * @return 친구 요청 수를 조회하는 {@link JPAQuery}
     */
    private JPAQuery<Long> getRequestCountQuery(Long id) {
        return getRequestQueryBase(id, user.count());
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

    /**
     * 회원의 친구 요청 조회 시 조회 테이블, 조인 조건 등이 지정된 쿼리를 반환함.
     *
     * @param id 조회할 회원 ID
     * @param selectExpression {@select} 쿼리에서 조회할 컬럼이 지정된 {@link Expression}
     * @return {@code select} 조회 결과
     * @param <T> 쿼리 조회 후 반환할 클래스 타입
     */
    private <T> JPAQuery<T> getRequestQueryBase(Long id, Expression<T> selectExpression) {
        return queryFactory.select(selectExpression)
                .from(user)
                .join(friendRequest)
                .on(friendRequest.requester.id.eq(user.id))
                .where(friendRequest.receiver.id.eq(id));
    }
}
