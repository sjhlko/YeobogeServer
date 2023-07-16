package com.yeoboge.server.vo;

import lombok.Builder;

@Builder
public record ErrorResponse(String code, String message) { }
