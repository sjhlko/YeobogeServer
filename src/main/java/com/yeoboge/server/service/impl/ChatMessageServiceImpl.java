package com.yeoboge.server.service.impl;

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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    @Override
    public void saveMessage(String message, Long chatRoomId, Long userId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isEmpty()) throw new AppException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND);
        User user = userRepository.getById(userId);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom.get())
                .message(message)
                .user(user)
                .isRead(IsRead.NO)
                .build();
        chatMessageRepository.save(chatMessage);
    }
}
