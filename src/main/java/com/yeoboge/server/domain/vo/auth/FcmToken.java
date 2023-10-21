package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;
/**
 * Fcm token을 담은 VO
 *
 * @param token 특정 기기(특정 회원) 에 대한 fcm 토큰
 */

@Builder
public record FcmToken(String token) {
}
