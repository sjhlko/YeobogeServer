package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.user.ProfileImgResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final S3FileUploadServiceImpl s3FileUploadService;
    @Override
    public UserDetailResponse getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new AppException(AuthenticationErrorCode.USER_NOT_FOUND,AuthenticationErrorCode.USER_NOT_FOUND.getMessage()));
        return UserDetailResponse.of(user);
    }

    @Override
    public ProfileImgResponse changeProfileImg(MultipartFile file, Long id) {
        User existedUser = userRepository.findById(id)
                .orElseThrow(()->new AppException(AuthenticationErrorCode.USER_NOT_FOUND,AuthenticationErrorCode.USER_NOT_FOUND.getMessage()));
        User updatedUser = User.updateProfileImagePath(existedUser, s3FileUploadService.uploadFile(file));
        userRepository.save(updatedUser);
        return ProfileImgResponse.builder()
                .url(updatedUser.getProfileImagePath())
                .build();

    }
}
