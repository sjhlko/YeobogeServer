package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * HTTP 기본 에러 관련 {@link ErrorCode}
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "This request is unacceptable"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Requested Resource Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, try again in a bit");

    private final HttpStatus httpStatus;
    private final String message;
}
