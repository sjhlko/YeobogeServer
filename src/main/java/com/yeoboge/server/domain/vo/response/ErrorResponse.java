package com.yeoboge.server.domain.vo.response;

import lombok.Builder;

/**
 * HTTP 에러 응답 시 클라이언트에 전달할 VO
 *
 * @param code HTTP 에러 코드 명
 * @param message 에러 메세지
 */
@Builder
public record ErrorResponse(String code, String message) { }
