package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.Friend;
import com.yeoboge.server.domain.entity.FriendRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.user.RequestFriendRequest;
import com.yeoboge.server.enums.error.FriendRequestErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.FriendRequestRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.FriendService;
import com.yeoboge.server.service.PushAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * {@link FriendService} 구현체
 */
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final PushAlarmService pushAlarmService;
    private final TokenRepository tokenRepository;

    @Override
    public PageResponse getFriends(Long id, Pageable pageable) {
        Page friendsPage = friendRepository.getFriendsPage(id, pageable);
        return new PageResponse(friendsPage);
    }

    @Override
    public PageResponse getFriendRequests(Long id, Pageable pageable) {
        Page requestPage = friendRequestRepository.getFriendRequestsPage(id, pageable);
        return new PageResponse(requestPage);
    }

    @Override
    public UserInfoDto searchUserByNickname(String nickname) {
        User user = userRepository.getByNickname(nickname);
        return UserInfoDto.of(user);
    }

    @Override
    public MessageResponse requestFriend(Long id, RequestFriendRequest request) {
        User requester = userRepository.getById(id);
        User receiver = userRepository.getById(request.id());
        if (friendRequestRepository.existsByReceiverIdAndRequesterId(receiver.getId(), requester.getId())) {
            throw new AppException(FriendRequestErrorCode.REQUEST_ALREADY_EXISTS);
        }
        checkFriend(receiver.getId(), requester.getId());
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build();
        friendRequestRepository.save(friendRequest);
        Optional<String> fcmToken = tokenRepository.findFcmToken(request.id());
        if (fcmToken.isPresent()) {
            PushAlarmRequest pushAlarmRequest = PushAlarmRequest.builder()
                    .pushAlarmType(PushAlarmType.FRIEND_REQUEST)
                    .currentUserId(id)
                    .targetToken(fcmToken.get())
                    .build();
            pushAlarmService.sendPushAlarm(pushAlarmRequest);
        }
        return MessageResponse.builder()
                .message("친구 요청이 성공적으로 전송되었습니다.")
                .build();
    }

    @Override
    public MessageResponse acceptFriendRequest(Long currentUserId, Long id) {
        User requester = userRepository.getById(id);
        User receiver = userRepository.getById(currentUserId);
        FriendRequest friendRequest = friendRequestRepository.getByReceiverIdAndRequesterId(currentUserId, id);
        checkFriend(receiver.getId(), requester.getId());
        friendRequestRepository.delete(friendRequest);
        if (friendRequestRepository.existsByReceiverIdAndRequesterId(id, currentUserId)) {
            friendRequestRepository.delete(friendRequestRepository.getByReceiverIdAndRequesterId(id, currentUserId));
        }
        Friend friend = Friend.builder()
                .follower(requester)
                .owner(receiver)
                .build();
        friendRepository.save(friend);
        return MessageResponse.builder()
                .message("친구 요청이 성공적으로 수락되었습니다.")
                .build();
    }

    @Override
    public MessageResponse denyFiendRequest(Long currentUserId, Long id) {
        User requester = userRepository.getById(id);
        User receiver = userRepository.getById(currentUserId);
        FriendRequest friendRequest = friendRequestRepository.getByReceiverIdAndRequesterId(currentUserId, id);
        checkFriend(receiver.getId(), requester.getId());
        friendRequestRepository.delete(friendRequest);
        return MessageResponse.builder()
                .message("친구 요청이 성공적으로 거절되었습니다.")
                .build();
    }


    /**
     * 두 회원이 이미 회원인지 확인함
     *
     * @param followerId 친구인지 확인할 유저 중 한명
     * @param ownerId    친구인지 확인할 유저 중 나머지 한명
     * @throws {@link AppException}
     */
    private void checkFriend(Long followerId, Long ownerId) {
        if (friendRepository.existsByFollowerIdAndOwnerId(followerId, ownerId) ||
                friendRepository.existsByFollowerIdAndOwnerId(ownerId, followerId)
        ) {
            throw new AppException(FriendRequestErrorCode.IS_ALREADY_FRIEND);
        }
    }
}
