package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;

public interface UserService {
    UserDetailResponse getProfile(Long id);
}
