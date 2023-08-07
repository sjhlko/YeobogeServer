package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

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
    void getProfileFailed1() {
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
    void updateUser() {
    }
}