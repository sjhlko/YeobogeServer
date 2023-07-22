package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;

@Builder
public record Tokens(String accessToken, String refreshToken) { }
