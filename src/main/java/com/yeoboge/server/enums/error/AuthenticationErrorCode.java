package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 계정 및 인증 관련 {@link ErrorCode}
 */
@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {
    USER_DUPLICATED(HttpStatus.BAD_REQUEST, "User with this property is already existed"),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "Email doesn't exist"),
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "Username or password is invalid"),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"User doesn't exist"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid");

    private final HttpStatus httpStatus;
    private final String message;
}
