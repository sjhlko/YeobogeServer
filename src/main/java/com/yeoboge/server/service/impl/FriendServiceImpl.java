package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.entity.FriendRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.user.RequestFriendRequest;
import com.yeoboge.server.enums.error.FriendRequestErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.FriendRequestRepository;
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
    private final FriendRequestRepository friendRequestRepository;

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

    @Override
    public MessageResponse requestFriend(Long id, RequestFriendRequest request) {
        User requester = userRepository.getById(id);
        User receiver = userRepository.getById(request.id());
        if (friendRequestRepository.existsByReceiverIdAndRequesterId(receiver.getId(), requester.getId())){
            throw new AppException(FriendRequestErrorCode.REQUEST_ALREADY_EXISTS);
        }
        if (friendRepository.existsByFollowerIdAndOwnerId(receiver.getId(), requester.getId()) ||
                friendRepository.existsByFollowerIdAndOwnerId(requester.getId(), receiver.getId())
        ){
            throw new AppException(FriendRequestErrorCode.IS_ALREADY_FRIEND);
        }
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build();
        friendRequestRepository.save(friendRequest);
        return MessageResponse.builder()
                .message("친구 요청이 성공적으로 전송되었습니다.")
                .build();
    }
}
