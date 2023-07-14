package com.yeoboge.server.vo.auth;

import lombok.Builder;

@Builder
public record LoginResponse(String accessToken, String refreshToken) { }
