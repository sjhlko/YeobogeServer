package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.vo.auth.Tokens;

/**
 * 소셜 계정으로 회원 가입 및 로그인 비즈니스 로직에 대한 메서드를 제공하는 Service
 */
public interface OAuthService {
    /**
     * 소셜 계정으로 사용자를 회원 가입하고 해당 사용자의 JWT 토큰을 발급함.
     *
     * @param request 소셜 계정으로 회원 가입 시 기본 정보를 담고 있는 {@link SocialRegisterRequest}
     * @return 발급된 Access Token, Refresh Token를 담고 있는 {@link Tokens}
     */
    Tokens socialRegister(SocialRegisterRequest request);

    /**
     * 소셜 계정으로 사용자를 로그인하고 JWT 토큰을 발급함.
     *
     * @param email 로그인할 소셜 계정 이메일
     * @return 발급된 Access Token, Refresh Token를 담고 있는 {@link Tokens}
     */
    Tokens socialLogin(String email);
}
