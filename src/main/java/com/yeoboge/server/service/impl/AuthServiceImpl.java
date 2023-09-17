package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.AuthService;
import com.yeoboge.server.service.MailService;
import com.yeoboge.server.helper.utils.StringGeneratorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link AuthService} 구현체
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final int TOKEN_SPLIT_INDEX = 7;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailService mailService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new AppException(AuthenticationErrorCode.USER_DUPLICATED);

        List<Genre> favoriteGenres = genreRepository.findAllById(request.favoriteGenreIds());
        User newUser = request.toEntity(encodePassword(request.password()), favoriteGenres);

        User saved = userRepository.save(newUser);
        return RegisterResponse.fromEntity(saved);
    }

    @Override
    public MessageResponse checkEmailDuplication(String email) {
        if (userRepository.existsByEmail(email))
            throw new AppException(AuthenticationErrorCode.USER_DUPLICATED);

        return MessageResponse.builder()
                .message(email + ": 사용 가능한 이메일")
                .build();
    }

    @Override
    public MessageResponse checkNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname))
            throw new AppException(AuthenticationErrorCode.USER_DUPLICATED);

        return MessageResponse.builder()
                .message(nickname + ": 사용 가능한 닉네임")
                .build();
    }

    @Override
    public Tokens login(LoginRequest request) {
        String username = request.email();
        String password = request.password();
        authenticate(username, password);

        Long userId = userRepository.findIdByEmail(username);

        return generateToken(userId);
    }

    @Override
    public MessageResponse logout(String header) {
        String accessToken = header.substring(TOKEN_SPLIT_INDEX);

        tokenRepository.delete(accessToken);

        return MessageResponse.builder()
                .message("로그아웃 성공")
                .build();
    }

    @Override
    public MessageResponse makeTempPassword(GetResetPasswordEmailRequest request) {
        String tempPassword = StringGeneratorUtils.getTempPassword();
        User existedUser = userRepository.getByEmail(request.email());
        existedUser.updatePassword(encodePassword(tempPassword));
        userRepository.save(existedUser);
        mailService.makePassword(tempPassword);
        mailService.sendEmail(existedUser);
        return MessageResponse.builder()
                .message("이메일 발송됨")
                .build();
    }

    @Override
    public MessageResponse updatePassword(UpdatePasswordRequest request, Long id) {
        User existedUser = userRepository.getById(id);
        if (!passwordEncoder.matches(request.existingPassword(), existedUser.getPassword()))
            throw new AppException(AuthenticationErrorCode.PASSWORD_NOT_MATCH);
        existedUser.updatePassword(encodePassword(request.updatedPassword()));
        userRepository.save(existedUser);
        return MessageResponse.builder()
                .message("비밀번호 변경 성공")
                .build();
    }

    @Override
    public MessageResponse unregister(Long id, String authorizationHeader) {
        User user = userRepository.getById(id);
        userRepository.delete(user);
        tokenRepository.delete(authorizationHeader.substring(TOKEN_SPLIT_INDEX));
        return MessageResponse.builder()
                .message("회원 탈퇴 성공")
                .build();
    }

    @Override
    public Tokens refreshTokens(Tokens tokens) {
        String accessToken = tokens.accessToken();
        String refreshToken = tokenRepository.getByToken(accessToken);

        tokens.checkTokenValidation(refreshToken);

        tokenRepository.delete(accessToken);
        Long userId = jwtProvider.parseUserId(refreshToken);

        return generateToken(userId);
    }

    /**
     * 비밀번호 암호화를 위해 해시를 적용함.
     *
     * @param password 비밀번호 plain text
     * @return 해싱된 비밀번호
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Spring Security 인증 메커니즘으로 사용자 계정 인증 후 Context 저장함.
     *
     * @param username 계정 이메일
     * @param password 계정 비밀번호
     * @see UsernamePasswordAuthenticationToken
     * @see AuthenticationManager
     */
    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authManager.authenticate(authToken);
    }

    /**
     * {@link User} ID로 해당 사용자의 토큰들을 발급하고
     * Redis 스토리지에 저장함.
     *
     * @param userId {@link User} ID
     * @return 발급된 토큰들을 담은 {@link Tokens}
     * @see JwtProvider
     * @see TokenRepository
     */
    private Tokens generateToken(long userId) {
        Tokens tokens = jwtProvider.generateTokens(userId);
        tokenRepository.save(tokens);

        return tokens;
    }
}
