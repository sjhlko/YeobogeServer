package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Token;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.AuthService;
import com.yeoboge.server.utils.MakeEmail;
import com.yeoboge.server.utils.MakeTempPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JavaMailSender javaMailSender;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new AppException(AuthenticationErrorCode.EXISTED_USERNAME);

        List<Genre> favoriteGenres = genreRepository.findAllById(request.favoriteGenreIds());
        User newUser = request.toEntity(
                encodePassword(request.password()),
                new HashSet<>(favoriteGenres)
        );

        User saved = userRepository.save(newUser);
        return RegisterResponse.fromEntity(saved);
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
    public TempPasswordResponse makeTempPassword(GetResetPasswordEmailRequest request) {
        String tempPassword = MakeTempPassword.getTempPassword();
        User existedUser = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new AppException(AuthenticationErrorCode.EMAIL_INVALID));
        User updatedUser = User.updatePassword(existedUser,encodePassword(tempPassword));
        userRepository.save(updatedUser);
        MakeEmail makeEmail = new MakeEmail(tempPassword);
        makeEmail.sendEmail(updatedUser,javaMailSender);
        return TempPasswordResponse.builder()
                .message("이메일 발송됨")
                .build();
    }

    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest request, Object principal) {
        User existedUser = userRepository.findById((Long) principal)
                .orElseThrow(()->new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(request.existingPassword(), existedUser.getPassword()))
            throw new AppException(AuthenticationErrorCode.PASSWORD_NOT_MATCH);
        User updatedUser = User.updatePassword(existedUser,encodePassword(request.updatedPassword()));
        userRepository.save(updatedUser);
        return UpdatePasswordResponse.builder()
                .message("비밀번호 변경 성공")
                .build();
    }

    @Override
    public UnregisterResponse unregister(Authentication authentication, String authorizationHeader) {
        User user = userRepository.findById((Long) authentication.getPrincipal())
                .orElseThrow(()->new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        tokenRepository.delete(authorizationHeader.substring(7));
        return UnregisterResponse.builder()
                .message("회원 탈퇴 성공")
                .build();
    }

    @Override
    public Tokens refreshTokens(Tokens tokens) {
        String accessToken = tokens.accessToken();
        String refreshToken = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.TOKEN_INVALID));

        if (!refreshToken.equals(tokens.refreshToken()))
            throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);

        tokenRepository.delete(accessToken);
        Long userId = jwtProvider.parseUserId(refreshToken);

        return generateToken(userId);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authManager.authenticate(authToken);
    }

    private Tokens generateToken(long userId) {
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        tokenRepository.save(new Token(accessToken, refreshToken));

        return Tokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
