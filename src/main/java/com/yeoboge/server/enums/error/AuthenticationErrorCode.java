package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {
    EXISTED_USERNAME(HttpStatus.BAD_REQUEST, "Email is already existed"),
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "Username or password is invalid"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid");

    private final HttpStatus httpStatus;
    private final String message;
}
