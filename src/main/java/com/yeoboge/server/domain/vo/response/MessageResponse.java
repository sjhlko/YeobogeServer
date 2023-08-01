package com.yeoboge.server.domain.vo.response;

import lombok.Builder;

/**
 * 메세지만 포함된 HTTP 응답을 클라이언트에 전달하기 위한 VO
 *
 * @param message HTTP 응답 메세지
 */
@Builder
public record MessageResponse(String message) { }
