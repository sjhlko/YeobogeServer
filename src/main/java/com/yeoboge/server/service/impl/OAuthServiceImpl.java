package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.dto.auth.UserDetailsDto;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.SocialLoginRequest;
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

/**
 * {@link OAuthService} 구현체
 */
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

        return generateToken(user.getId());
    }

    @Override
    public Tokens socialLogin(SocialLoginRequest request) {
        String email = request.email();
        UserDetailsDto user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        if (user.password() != null)
            throw new AppException(AuthenticationErrorCode.BAD_CREDENTIAL);

        return generateToken(user.id());
    }

    /**
     * JWT 토큰을 발급하고 해당 토큰을 Redis 스토리지에 저장함.
     *
     * @param userId {@link User} ID
     * @return Access Token, Refresh Token을 담고 있는 {@link Tokens}
     * @see JwtProvider
     */
    private Tokens generateToken(Long userId) {
        Tokens tokens = jwtProvider.generateTokens(userId);
        tokenRepository.save(tokens);

        return tokens;
    }
}
