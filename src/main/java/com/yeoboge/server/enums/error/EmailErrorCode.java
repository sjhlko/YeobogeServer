package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 이메일 전송 관련 {@link ErrorCode}
 */
@Getter
@RequiredArgsConstructor
public enum EmailErrorCode implements ErrorCode{
    EMAIL_SENDING_ERROR(HttpStatus.BAD_REQUEST, "Email sending error");
    private final HttpStatus httpStatus;
    private final String message;
}
