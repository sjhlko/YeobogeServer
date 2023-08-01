package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;

/**
 * 계정 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface AuthService {
    /**
     * 사용자 회원 가입 메서드
     *
     * @param request 가입할 사용자의 기본 정보를 담고 있는 {@link RegisterRequest} DTO
     * @return {@link RegisterResponse}
     * @see com.yeoboge.server.enums.error.AuthenticationErrorCode
     * @see com.yeoboge.server.handler.AppException
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 이메일 중복 여부를 확인하고 중복일 경우 중복 예외를 던짐
     *
     * @param email 확인할 계정 이메일
     * @return {@link MessageResponse}
     * @see com.yeoboge.server.enums.error.AuthenticationErrorCode
     * @see com.yeoboge.server.handler.AppException
     */
    MessageResponse checkEmailDuplication(String email);

    /**
     * 로그인 후 해당 사용자의 Access Token, Refresh Token을 발급함.
     * 로그인 실패 시 인증 예외를 던짐.
     *
     * @param request 로그인할 계정 이메일, 비밀번호를 담은 {@link LoginRequest} VO
     * @return 발급된 Access Token, Refresh Token {@link Tokens}
     */
    Tokens login(LoginRequest request);

    /**
     * 로그아웃 후 사용자의 토큰들을 삭제함.
     *
     * @param header 사용자의 Access Token
     * @return {@link MessageResponse}
     */
    MessageResponse logout(String header);
    MessageResponse makeTempPassword(GetResetPasswordEmailRequest request);
    MessageResponse updatePassword(UpdatePasswordRequest request, Long id);
    MessageResponse unregister(Long id, String authorizationHeader);

    /**
     * 사용자의 Access Token, Refresh Token을 재밟급함.
     *
     * @param tokens 기존 토큰들을 담은 {@link Tokens}
     * @return 재발급된 토큰들을 담은 {@link Tokens}
     */
    Tokens refreshTokens(Tokens tokens);
}
