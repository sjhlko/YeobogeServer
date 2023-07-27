package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.user.UpdateUser;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDetailResponse getProfile(Long id);
    MessageResponse updateUser(MultipartFile file, UserUpdateRequest request, Long id);
}
