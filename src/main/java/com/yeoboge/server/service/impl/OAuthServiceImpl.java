package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final JwtProvider jwtProvider;

    @Override
    public Tokens socialRegister(SocialRegisterRequest request) {
        List<Genre> favoriteGenres = genreRepository.findAllById(request.favoriteGenreIds());
        User user = request.toEntity(new HashSet<>(favoriteGenres));
        user = userRepository.save(user);

        return generateTokens(user.getId());
    }

    @Override
    public Tokens socialLogin(String email) {
        if (!userRepository.existsByEmail(email))
            throw new AppException(AuthenticationErrorCode.USER_NOT_FOUND);

        Long userId = userRepository.findIdByEmail(email);

        return generateTokens(userId);
    }

    private Tokens generateTokens(Long userId) {
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        tokenRepository.save(new Token(accessToken, refreshToken));

        return Tokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
