package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.vo.auth.SocialLoginRequest;
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
     * 자체 회원가입으로 가입한 사용자의 경우 비밀번호를 사용해 로그인하도록
     * {@link com.yeoboge.server.enums.error.AuthenticationErrorCode}의 {@code BAD_CREDENTIAL} 예외를 던짐.
     *
     * @param request 로그인할 소셜 계정 이메일을 담고 있는 {@link SocialLoginRequest}
     * @return 발급된 Access Token, Refresh Token를 담고 있는 {@link Tokens}
     */
    Tokens socialLogin(SocialLoginRequest request);
}
