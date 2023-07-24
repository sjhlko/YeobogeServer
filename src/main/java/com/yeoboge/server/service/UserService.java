package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.vo.user.ProfileImgResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDetailResponse getProfile(Long id);
    ProfileImgResponse changeProfileImg(MultipartFile file, Long id);
}
