package com.yeoboge.server.repository.impl;

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

    /**
     * 해당 회원의 친구 목록을 페이징 크기만큼 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return {@link FriendInfoDto} 리스트
     */
    private List<FriendInfoDto> getFriendList(Long id, Pageable pageable) {
        return queryFactory.select(Projections.constructor(FriendInfoDto.class,
                        user.id,
                        user.nickname,
                        user.profileImagePath.as("imagePath")
                )).from(user)
                .join(friend)
                .on(friend.follower.id.eq(user.id))
                .where(friend.owner.id.eq(id))
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
        return queryFactory.select(user.count())
                .from(user)
                .join(friend)
                .on(friend.owner.id.eq(user.id))
                .where(user.id.eq(id));
    }
}
