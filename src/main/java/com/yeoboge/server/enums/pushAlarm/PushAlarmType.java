package com.yeoboge.server.enums.pushAlarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushAlarmType {
    CHATTING("chatting"),
    RATING("rating"),
    FRIEND("friend");
    private String key;
}
