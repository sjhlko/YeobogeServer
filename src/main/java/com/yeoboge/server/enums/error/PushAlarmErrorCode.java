package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PushAlarmErrorCode implements ErrorCode{
    SENDING_PUSH_ALARM_ERROR(HttpStatus.BAD_REQUEST, "Push alarm sending error."),
    CAN_NOT_MAKE_ACCESS_TOKEN(HttpStatus.BAD_REQUEST,"can't make access token");

    private final HttpStatus httpStatus;
    private final String message;
}
