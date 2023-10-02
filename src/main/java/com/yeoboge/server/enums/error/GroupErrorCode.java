package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {
    NO_GROUP_MEMBER(HttpStatus.NOT_FOUND, "No group member founded."),
    OVER_LIMIT_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "Group member overed limit.");

    private final HttpStatus httpStatus;
    private final String message;
}
