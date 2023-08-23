package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChattingErrorCode implements ErrorCode{
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Chat room doesn't exist");
    private final HttpStatus httpStatus;
    private final String message;
}
