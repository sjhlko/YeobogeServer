package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;

@Builder
public record FcmToken(String token) {
}
