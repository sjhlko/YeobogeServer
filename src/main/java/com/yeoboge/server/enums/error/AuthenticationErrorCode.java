package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {
    EXISTED_USERNAME(HttpStatus.BAD_REQUEST, "Email is already existed"),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "Email doesn't exist"),
    CAN_NOT_UNREGISTER(HttpStatus.BAD_REQUEST, "Can't unregister : access token is invalid"),
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "Username or password is invalid"),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"User doesn't exist"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid");

    private final HttpStatus httpStatus;
    private final String message;
}
