package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailErrorCode implements ErrorCode{
    EMAIL_SENDING_ERROR(HttpStatus.BAD_REQUEST, "Email sending error");
    private final HttpStatus httpStatus;
    private final String message;
}
