package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.repository.ChatRoomRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public Long findChatRoomIdByUsers(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.getById(currentUserId);
        User targetUser = userRepository.getById(targetUserId);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCurrentUserAndTargetUser(currentUser, targetUser);
        if (chatRoom.isEmpty()) chatRoom = chatRoomRepository.findByCurrentUserAndTargetUser(targetUser, currentUser);
        if (chatRoom.isEmpty()) {
            ChatRoom newChatRoom = ChatRoom.builder()
                    .currentUser(currentUser)
                    .targetUser(targetUser)
                    .build();
            newChatRoom = chatRoomRepository.save(newChatRoom);
            return newChatRoom.getId();
        }
        return chatRoom.get().getId();
    }
}
