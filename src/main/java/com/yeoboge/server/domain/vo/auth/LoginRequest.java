package com.yeoboge.server.domain.vo.auth;

/**
 * 로그인에 사용되는 계정 정보를 담은 VO
 *
 * @param email 계정 이메일
 * @param password 계정 비밀번호
 */
public record LoginRequest(String email, String password) { }
