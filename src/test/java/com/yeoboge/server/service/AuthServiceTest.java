package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.error.EmailErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private MailService mailService;

    @Test
    @DisplayName("회원가입 성공 단위 테스트")
    public void registerSuccess() {
        // given
        Long userId = 1L;
        String userCode = "user_code";
        String hashedPassword = "hashed_password";
        List<Genre> favoriteGenres = List.of(
                Genre.builder().id(1L).build(),
                Genre.builder().id(2L).build()
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
    @DisplayName("중복 이메일 확인 단위 테스트")
    public void checkEmailAvailableSuccess() {
        // given
        String email = "not@existed.com";
        MessageResponse expected = MessageResponse.builder()
                .message(email + ": 사용 가능한 이메일")
                .build();

        // when
        when(userRepository.existsByEmail(email)).thenReturn(false);

        MessageResponse actual = authService.checkEmailDuplication(email);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 중복 확인 단위 테스트")
    public void checkEmailAvailableFail() {
        // given
        String email = "already@existed.com";

        // when
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // then
        assertThatThrownBy(() -> authService.checkEmailDuplication(email))
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
        Tokens expected = makeTokens(expectedAccessToken, expectedRefreshToken);

        // when
        when(userRepository.findIdByEmail(username)).thenReturn(userId);
        when(jwtProvider.generateTokens(userId)).thenReturn(expected);

        Tokens actual = authService.login(request);

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

    @Test
    @DisplayName("로그아웃 성공 단위 테스트")
    public void logoutSuccess() {
        // given
        String header = "Bearer access_token";
        String accessToken = "access_token";
        MessageResponse expected = MessageResponse.builder()
                .message("로그아웃 성공")
                .build();

        // when
        doNothing().when(tokenRepository).delete(accessToken);

        MessageResponse actual = authService.logout(header);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그아웃 실패 단위 테스트")
    public void logoutFail() {
        // given
        String header = "Bearer invalid_access_token";
        String invalidToken = "invalid_access_token";

        // when
        doThrow(AppException.class).when(tokenRepository).delete(invalidToken);

        // then
        assertThatThrownBy(() -> authService.logout(header))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 성공 단위 테스트")
    public void refreshTokensSuccess() {
        // given
        String prevAccessToken = "previous_access_token";
        String prevRefreshToken = "previous_refresh_token";
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";
        Tokens expected = makeTokens(newAccessToken, newRefreshToken);
        Long userId = 1L;

        // when
        when(tokenRepository.getByToken(prevAccessToken)).thenReturn(prevRefreshToken);
        when(jwtProvider.parseUserId(prevRefreshToken)).thenReturn(userId);
        when(jwtProvider.generateTokens(userId)).thenReturn(expected);

        Tokens actual = authService.refreshTokens(makeTokens(prevAccessToken, prevRefreshToken));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 액세스 토큰 재발급 실패 단위 테스트")
    public void refreshTokensFailByWrongToken() {
        // given
        String nonExistedAccessToken = "non_existed_access_token";
        String refreshToken = "refresh_token";
        Tokens tokens = makeTokens(nonExistedAccessToken, refreshToken);

        // when
        when(tokenRepository.getByToken(nonExistedAccessToken)).thenThrow(new AppException());

        // then
        assertThatThrownBy(() -> authService.refreshTokens(tokens))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("리프레시 토큰 검증 실패 단위 테스트")
    public void refreshTokensFailByDifferentToken() {
        // given
        String accessToken = "access_token";
        String wrongRefreshToken = "wrong_refresh_token";
        String actualRefreshToken = "actual_refresh_token";
        Tokens tokens = makeTokens(accessToken, wrongRefreshToken);

        // when
        when(tokenRepository.getByToken(accessToken)).thenReturn(actualRefreshToken);

        // then
        assertThatThrownBy(() -> authService.refreshTokens(tokens))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰 재발급 실패 단위 테스트")
    public void refreshTokensFailByExpiredToken() {
        // given
        String accessToken = "access_token";
        String expiredRefreshToken = "expired_refresh_token";
        Tokens tokens = makeTokens(accessToken, expiredRefreshToken);

        // when
        when(tokenRepository.getByToken(accessToken)).thenReturn(expiredRefreshToken);
        when(jwtProvider.parseUserId(expiredRefreshToken))
                .thenThrow(new AppException(AuthenticationErrorCode.TOKEN_INVALID));

        // then
        assertThatThrownBy(() -> authService.refreshTokens(tokens))
                .isInstanceOf(AppException.class);
    }


    @Test
    @DisplayName("임시 비밀번호 메일 발송_성공")
    void makeTempPasswordSuccess() {
        // given
        User user = User.builder()
                .email("aaa@gmail.com")
                .build();
        GetResetPasswordEmailRequest request = GetResetPasswordEmailRequest.builder()
                .email(user.getEmail())
                .build();

        // when
        when(userRepository.getByEmail(any()))
                .thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(mailService).makePassword(any());
        doNothing().when(mailService).sendEmail(user);

        // then
        assertThat(authService.makeTempPassword(request)).isEqualTo(MessageResponse.builder()
                .message("이메일 발송됨")
                .build());

    }

    @Test
    @DisplayName("임시 비밀번호 메일 발송_실패 : 회원가입된 이메일이 아님")
    void makeTempPasswordFailed1() {
        // given
        GetResetPasswordEmailRequest request = GetResetPasswordEmailRequest.builder()
                .email("aaa@gmail.com")
                .build();

        // when
        when(userRepository.getByEmail(request.email()))
                .thenThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> authService.makeTempPassword(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("임시 비밀번호 메일 발송_실패 : 이메일 전송 오류")
    void makeTempPasswordFailed2() {
        // given
        User user = User.builder()
                .email("aaa@gmail.com")
                .build();
        GetResetPasswordEmailRequest request = GetResetPasswordEmailRequest.builder()
                .email(user.getEmail())
                .build();

        // when
        when(userRepository.getByEmail(any()))
                .thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(mailService).makePassword(any());
        doThrow(new AppException(EmailErrorCode.EMAIL_SENDING_ERROR)).when(mailService).sendEmail(user);

        // then
        assertThatThrownBy(() -> authService.makeTempPassword(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(EmailErrorCode.EMAIL_SENDING_ERROR.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경_성공")
    void updatePasswordSuccess() {
        // given
        User user = User.builder()
                .id(0L)
                .password("aaa")
                .build();
        User updatedUser = User.builder()
                .id(user.getId())
                .password(passwordEncoder.encode(user.getPassword()))
                .build();
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .existingPassword(user.getPassword())
                .updatedPassword("bbb")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(passwordEncoder.matches(request.existingPassword(),user.getPassword())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(updatedUser);

        // then
        assertThat(userRepository.save(user).getPassword()).isEqualTo(passwordEncoder.encode(user.getPassword()));
        assertThat(authService.updatePassword(request,0L)).isEqualTo(MessageResponse.builder()
                .message("비밀번호 변경 성공")
                .build());
    }

    @Test
    @DisplayName("비밀번호 변경_실패 : 유저 존재하지 않음")
    void updatePasswordFailed1() {
        // given
        User user = User.builder()
                .id(0L)
                .password("aaa")
                .build();
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .existingPassword(user.getPassword())
                .updatedPassword("bbb")
                .build();

        // when
        doThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND))
                .when(userRepository).getById(user.getId());
        // then
        assertThatThrownBy(() -> authService.updatePassword(request,user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경_실패 : 기존 패스워드 불일치")
    void updatePasswordFailed2() {
        // given
        User user = User.builder()
                .id(0L)
                .password("aaa")
                .build();
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .existingPassword("ccc")
                .updatedPassword("bbb")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(passwordEncoder.matches(request.existingPassword(),user.getPassword())).thenReturn(false);

        // then
        assertThatThrownBy(() -> authService.updatePassword(request,user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.PASSWORD_NOT_MATCH.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴_성공")
    void unregisterSuccess() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        String header = "Bearer valid_access_token";

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        doNothing().when(userRepository).delete(user);
        doNothing().when(tokenRepository).delete(header.substring(7));

        // then
        assertThat(authService.unregister(user.getId(),header)).isEqualTo(MessageResponse.builder()
                .message("회원 탈퇴 성공")
                .build());

    }

    @Test
    @DisplayName("회원 탈퇴 실패 : 유저 존재하지 않음")
    void unregisterFailed1() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        String header = "Bearer valid_access_token";

        // when
        doThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND))
                .when(userRepository).getById(user.getId());

        // then
        assertThatThrownBy(() -> authService.unregister(user.getId(),header))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 : 토큰이 유효하지 않음")
    void unregisterFailed2() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        String header = "Bearer valid_access_token";

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        doNothing().when(userRepository).delete(user);
        doThrow(new AppException(AuthenticationErrorCode.TOKEN_INVALID))
                .when(tokenRepository).delete(header.substring(7));

        // then
        assertThatCode(() -> authService.unregister(user.getId(),header))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
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

    private Tokens makeTokens(String accessToken, String refreshToken) {
        return Tokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
