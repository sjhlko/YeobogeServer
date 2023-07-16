package com.yeoboge.server.service;

import com.yeoboge.server.security.JwtProvider;
import com.yeoboge.server.service.impl.AuthServiceImpl;
import com.yeoboge.server.domain.vo.auth.LoginRequest;
import com.yeoboge.server.domain.vo.auth.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationManager authManager;

    @Test
    @DisplayName("로그인 성공 단위 테스트")
    public void loginSuccess() {
        // given
        String username = "test@gmail.com";
        String password = "pass1234";
        String expectedAccessToken = "expected_access_token";
        String expectedRefreshToken = "expected_refresh_token";
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse expected = LoginResponse.builder()
                .accessToken(expectedAccessToken)
                .refreshToken(expectedRefreshToken)
                .build();

        // when
        when(jwtProvider.generateAccessToken(username)).thenReturn(expectedAccessToken);
        when(jwtProvider.generateRefreshToken(username)).thenReturn(expectedRefreshToken);

        LoginResponse actual = authService.login(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 실패 단위 테스트")
    public void wrongPasswordFail() {
        // given
        String username = "test@gmail.com";
        String password = "wrongpass";
        LoginRequest request = new LoginRequest(username, password);

        // when
        when(authManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
