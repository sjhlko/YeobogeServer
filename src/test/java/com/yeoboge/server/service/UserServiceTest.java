package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.error.UserErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3FileUploadService s3FileUploadService;

    @Test
    @DisplayName("회원 프로필 조회_성공")
    void getProfileSuccess() {
        // given
        User user = User.builder()
                .id(0L)
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);

        // then
        assertThat(userService.getProfile(user.getId())).isInstanceOf(UserDetailResponse.class);
    }

    @Test
    @DisplayName("회원 프로필 조회_실패 : 회원이 존재하지 않음")
    void getProfileFailed() {
        // given
        User user = User.builder()
                .id(0L)
                .build();

        // when
        when(userRepository.getById(user.getId())).thenThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.getProfile(user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 변경")
    void updateUserSuccess1() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8) );
        String imgUrl = "url";

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(s3FileUploadService.uploadFile(any())).thenReturn(imgUrl);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(file, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 유지")
    void updateUserSuccess2() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(false)
                .nickname("변경")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(null, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 삭제")
    void updateUserSuccess3() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(null, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_실패 : 회원이 존재하지 않음")
    void updateUserFailed1() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8) );

        // when
        when(userRepository.getById(user.getId()))
                .thenThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.updateUser(file, request, user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 프로필 변경_실패 : 프로필 사진 업로드 실패")
    void updateUserFailed2() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8));

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(s3FileUploadService.uploadFile(any())).thenThrow(
                new AppException(UserErrorCode.FILE_UPLOAD_ERROR)
        );

        // then
        assertThatThrownBy(() -> userService.updateUser(file, request, user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(UserErrorCode.FILE_UPLOAD_ERROR.getMessage());
    }
}