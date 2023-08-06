package com.yeoboge.server.service;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.SocialLoginRequest;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.OAuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {
    @InjectMocks
    private OAuthServiceImpl oAuthService;

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("소셜 로그인으로 회원가입 성공 단위 테스트")
    public void socialRegisterSuccess() {
        // given
        long userId = 1L;
        String userCode = "user_code";
        List<Genre> favoriteGenres = List.of(
                Genre.builder().id(1L).name("전략").build(),
                Genre.builder().id(2L).name("카드").build()
        );
        SocialRegisterRequest request = makeRegisterRequest();
        User expectedUser = User.builder().id(userId)
                .email(request.email())
                .nickname(request.nickname())
                .favoriteGenres(new HashSet<>(favoriteGenres))
                .userCode(userCode)
                .role(Role.USER)
                .build();
        Tokens expected = makeTokens();

        // when
        when(userRepository.save(any())).thenReturn(expectedUser);
        when(genreRepository.findAllById(List.of(1L, 2L))).thenReturn(favoriteGenres);
        when(jwtProvider.generateTokens(userId)).thenReturn(expected);

        Tokens actual = oAuthService.socialRegister(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("소셜 로그인 성공 단위 테스트")
    public void socialLoginSuccess() {
        // given
        long userId = 1L;
        String email = "test_email";
        SocialLoginRequest request = new SocialLoginRequest(email);
        Tokens expected = makeTokens();

        // when
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findIdByEmail(email)).thenReturn(userId);
        when(jwtProvider.generateTokens(userId)).thenReturn(expected);

        Tokens actual = oAuthService.socialLogin(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("소셜 로그인 실패 단위 테스트")
    public void socialLoginFail() {
        // given
        String email = "test_email";
        SocialLoginRequest request = new SocialLoginRequest(email);

        // when
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // then
        assertThatThrownBy(() -> oAuthService.socialLogin(request))
                .isInstanceOf(AppException.class);
    }

    private SocialRegisterRequest makeRegisterRequest() {
        String email = "test_email";
        String nickname = "test_nick";
        List<Long> favoriteGenreIds = List.of(1L, 2L);
        return new SocialRegisterRequest(email, nickname, favoriteGenreIds);
    }

    private Tokens makeTokens() {
        return Tokens.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .build();
    }
}
