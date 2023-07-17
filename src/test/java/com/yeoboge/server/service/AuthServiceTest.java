package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.RegisterResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

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
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 단위 테스트")
    public void registerSuccess() {
        // given
        Long userId = 1L;
        String userCode = "user_code";
        String hashedPassword = "hashed_password";
        List<Genre> favoriteGenres = List.of(
                new Genre(1L, "전략"),
                new Genre(2L, "카드")
        );
        RegisterRequest request = makeRegisterRequest();
        User expected = User.builder().id(userId)
                .email(request.email())
                .password(hashedPassword)
                .nickname(request.nickname())
                .favoriteGenres(new HashSet<>(favoriteGenres))
                .userCode(userCode)
                .role(Role.USER)
                .build();

        // when
        when(userRepository.save(any())).thenReturn(expected);
        when(genreRepository.findAllById(any())).thenReturn(favoriteGenres);
        when(passwordEncoder.encode(any())).thenReturn(hashedPassword);

        RegisterResponse actual = authService.register(request);

        // then
        assertThat(userId).isEqualTo(actual.id());
    }

    @Test
    @DisplayName("중복 이메일 회원가입 실패 단위 테스트")
    public void registerFailureByExistedEmail() {
        // given
        String email = "test@gmail.com";
        RegisterRequest request = makeRegisterRequest();

        // when
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("로그인 성공 단위 테스트")
    public void loginSuccess() {
        // given
        long userId = 1L;
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
        when(userRepository.findIdByEmail(username)).thenReturn(userId);
        when(jwtProvider.generateAccessToken(userId)).thenReturn(expectedAccessToken);
        when(jwtProvider.generateRefreshToken(userId)).thenReturn(expectedRefreshToken);

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

    private RegisterRequest makeRegisterRequest() {
        String email = "test@gmail.com";
        String password = "password";
        String nickname = "nick";
        List<Long> favoriteGenres = List.of(1L, 2L);

        return RegisterRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .favoriteGenreIds(favoriteGenres)
                .build();
    }
}
