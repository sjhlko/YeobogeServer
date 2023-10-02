package com.yeoboge.server.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.repository.CustomChatRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.yeoboge.server.domain.entity.QChatMessage.chatMessage;
import static com.yeoboge.server.domain.entity.QChatRoom.chatRoom;
import static com.yeoboge.server.domain.entity.QUser.user;

@Repository
public class CustomChatRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomChatRoomRepository {

    private final JPAQueryFactory queryFactory;

    public CustomChatRoomRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ChatRoom.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ChatRoomResponse> getMyChatRoomList(Pageable pageable, User currentUser) {

        List<ChatRoomResponse> results = new ArrayList<>();
        long count = 0;

        List<Tuple> subQuery = queryFactory
                .select(chatMessage.createdAt.max(), chatMessage.chatRoom.id)
                .from(chatMessage)
                .groupBy(chatMessage.chatRoom.id)
                .fetch();


        for (Tuple tuple : subQuery) {
            Predicate predicate = chatMessage.createdAt.eq(tuple.get(chatMessage.createdAt.max()))
                    .and(chatMessage.chatRoom.id.eq(tuple.get(chatMessage.chatRoom.id)));
            JPAQuery<ChatRoomResponse> query = queryFactory
                    .select(Projections.constructor(
                            ChatRoomResponse.class,
                            chatRoom.id,
                            chatMessage.message,
                            chatMessage.createdAt,
                            JPAExpressions.select(chatMessage.count())
                                    .from(chatMessage)
                                    .where(chatMessage.user.id.eq(user.id))
                                    .where(chatMessage.chatRoom.id.eq(chatRoom.id))
                                    .where(chatMessage.isRead.eq(IsRead.NO)),
                            Projections.constructor(
                                    UserInfoDto.class,
                                    user.id,
                                    user.nickname,
                                    user.profileImagePath
                            )
                    ))
                    .from(chatRoom)
                    .join(chatMessage)
                    .join(user)
                    .on(chatMessage.chatRoom.id.eq(chatRoom.id))
                    .on((chatRoom.targetUser.id.eq(user.id).and(chatRoom.currentUser.id.eq(currentUser.getId())))
                            .or(chatRoom.targetUser
                                    .id.eq(currentUser.getId()).and(chatRoom.currentUser.id.eq(user.id))))
                    .where(predicate)
                    .orderBy(chatMessage.createdAt.desc());
            count += query.fetch().size();
            List<ChatRoomResponse> chatRoomResponses = Objects.requireNonNull(this.getQuerydsl())
                    .applyPagination(pageable, query).fetch();
            results.addAll(chatRoomResponses);
        }
        List<ChatRoomResponse> sortedResults = results.stream()
                .sorted(Comparator.comparing(ChatRoomResponse::createdAt).reversed())
                .toList();
        return new PageImpl<>(sortedResults, pageable, count);
    }
}
