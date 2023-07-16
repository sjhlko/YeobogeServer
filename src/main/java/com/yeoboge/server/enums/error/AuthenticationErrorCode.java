package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "Username or password is invalid"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid");

    private final HttpStatus httpStatus;
    private final String message;
}
