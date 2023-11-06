package com.yeoboge.server.service.impl;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.SocialLoginRequest;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.utils.JwtTokenUtils;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        User user = request.toEntity(favoriteGenres);
        user = userRepository.save(user);

        return JwtTokenUtils.generateTokens(tokenRepository, jwtProvider, user.getId());
    }

    @Override
    public Tokens socialLogin(SocialLoginRequest request) {
        String email = request.email();
        User user = userRepository.getByEmail(email);

        if (user.getPassword() != null)
            throw new AppException(AuthenticationErrorCode.BAD_CREDENTIAL);

        return JwtTokenUtils.generateTokens(tokenRepository, jwtProvider, user.getId());
    }
}
