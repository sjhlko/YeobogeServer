package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * {@link FriendService} 구현체
 */
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse getFriends(Long id, Pageable pageable) {
        Page friendsPage = friendRepository.getFriendsPage(id, pageable);
        return new PageResponse(friendsPage);
    }

    @Override
    public PageResponse getFriendRequests(Long id, Pageable pageable) {
        Page requestPage = friendRepository.getFriendRequestsPage(id, pageable);
        return new PageResponse(requestPage);
    }

    @Override
    public FriendInfoDto searchUserByNickname(String nickname) {
        User user = userRepository.getByNickname(nickname);
        return FriendInfoDto.of(user);
    }
}
