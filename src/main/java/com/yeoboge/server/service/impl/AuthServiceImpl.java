package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.RegisterResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.security.JwtProvider;
import com.yeoboge.server.domain.vo.auth.LoginRequest;
import com.yeoboge.server.domain.vo.auth.LoginResponse;
import com.yeoboge.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public RegisterResponse register(RegisterRequest request) {
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

        return generateToken(username);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authManager.authenticate(authToken);
    }

    private LoginResponse generateToken(String username) {
        String accessToken = jwtProvider.generateAccessToken(username);
        String refreshToken = jwtProvider.generateRefreshToken(username);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
