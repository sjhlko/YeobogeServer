package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 보드게임 API 관련 {@link ErrorCode}
 */
@Getter
@RequiredArgsConstructor
public enum BoardGameErrorCode implements ErrorCode{
    BOARD_GAME_NOT_FOUND(HttpStatus.NOT_FOUND,"board game doesn't exist");
    private final HttpStatus httpStatus;
    private final String message;
}
