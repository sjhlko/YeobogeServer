package com.yeoboge.server.enums.pushAlarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushAlarmDataForChatting {
    id("id"),
    nickname("nickname"),
    img("imagePath");
    private String key;
}
