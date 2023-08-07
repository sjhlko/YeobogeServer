package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 프로필 조회_성공")
    void getProfile() {
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
    void updateUser() {
    }
}