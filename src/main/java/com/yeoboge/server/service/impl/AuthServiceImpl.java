package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Token;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.LoginRequest;
import com.yeoboge.server.domain.vo.auth.LoginResponse;
import com.yeoboge.server.domain.vo.auth.RegisterResponse;
import com.yeoboge.server.domain.vo.auth.TempPasswordResponse;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(AuthenticationErrorCode.EXISTED_USERNAME, "Email is already existed");
        }

        List<Genre> favoriteGenres = genreRepository.findAllById(request.favoriteGenreIds());
        User newUser = request.toEntity(
                encodePassword(request.password()),
                new HashSet<>(favoriteGenres)
        );

        User saved = userRepository.save(newUser);
        return RegisterResponse.fromEntity(saved);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.email();
        String password = request.password();
        authenticate(username, password);

        Long userId = userRepository.findIdByEmail(username);

        return generateToken(userId);
    }

    @Override
    public TempPasswordResponse makeTempPassword(String email) {
        String tempPassword = MakeTempPassword.getTempPassword();
        User existedUser = userRepository.findUserByEmail(email)
                .orElseThrow(()->new AppException(AuthenticationErrorCode.EMAIL_INVALID,AuthenticationErrorCode.EMAIL_INVALID.getMessage()));
        User updatedUser = User.updatePassword(existedUser,encodePassword(tempPassword));
        userRepository.save(updatedUser);
        MakeEmail makeEmail = new MakeEmail(tempPassword);
        makeEmail.sendEmail(email,javaMailSender);
        return TempPasswordResponse.builder()
                .message("이메일 발송됨")
                .build();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authManager.authenticate(authToken);
    }

    private LoginResponse generateToken(long userId) {
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        tokenRepository.save(new Token(accessToken, refreshToken));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
