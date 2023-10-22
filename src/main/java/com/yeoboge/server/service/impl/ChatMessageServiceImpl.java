package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.chat.ChatMessageResponse;
import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.IsRead;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.ChattingErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.ChatMessageRepository;
import com.yeoboge.server.repository.ChatRoomRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * {@link ChatMessageService} 구현체
 */
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Async
    public void saveMessage(String message, String timeStamp, Long chatRoomId, Long userId, IsRead isRead) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty()) throw new AppException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND);
        User user = userRepository.getById(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom.get())
                .message(message)
                .user(user)
                .createdAt(LocalDateTime.parse(timeStamp,formatter))
                .isRead(isRead)
                .build();
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public PageResponse getChatMessages(Long currentUserId, Long id, Pageable pageable) {
        User currentUser = userRepository.getById(currentUserId);
        User targetuser = userRepository.getById(id);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCurrentUserAndTargetUser(currentUser, targetuser);
        if (chatRoom.isEmpty()) chatRoom = chatRoomRepository.findByCurrentUserAndTargetUser(targetuser,currentUser);
        if (chatRoom.isEmpty()) throw new AppException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND);
        Page<ChatMessage> results = chatMessageRepository.findAllByChatRoom(pageable,chatRoom.get());
        PageResponse responses = new PageResponse(
                results.map(chatMessage -> ChatMessageResponse.of(chatMessage, currentUser)));
        return responses;
    }

    @Override
    @Async
    public void changeReadStatus(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.getById(chatRoomId);
        User currentUser = userRepository.getById(userId);
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomAndUserIsNotAndIsReadIs(
                chatRoom, currentUser, IsRead.NO);
        for (ChatMessage chatMessage : chatMessages) {
            chatMessage.updateReadStatus(IsRead.YES);
            chatMessageRepository.save(chatMessage);
        }
    }
}
