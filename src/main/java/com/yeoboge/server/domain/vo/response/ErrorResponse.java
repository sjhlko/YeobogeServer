package com.yeoboge.server.domain.vo.response;

import lombok.Builder;

@Builder
public record ErrorResponse(String code, String message) { }
