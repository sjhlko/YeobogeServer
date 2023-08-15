package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 친구 요청 관련 {@link ErrorCode}
 */
@Getter
@RequiredArgsConstructor
public enum FriendRequestErrorCode implements ErrorCode {
    REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "This request already exists"),
    REQUEST_INVALID(HttpStatus.NOT_FOUND, "Request doesn't exist"),
    IS_ALREADY_FRIEND(HttpStatus.BAD_REQUEST, "they are already friends");

    private final HttpStatus httpStatus;
    private final String message;
}
