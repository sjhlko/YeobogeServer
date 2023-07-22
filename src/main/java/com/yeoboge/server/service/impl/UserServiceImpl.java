package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDetailResponse getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        return UserDetailResponse.of(user);

    }
}
