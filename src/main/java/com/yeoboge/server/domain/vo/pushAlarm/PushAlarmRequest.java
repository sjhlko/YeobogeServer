package com.yeoboge.server.domain.vo.pushAlarm;

import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import lombok.Builder;

@Builder
public record PushAlarmRequest(
        PushAlarmType pushAlarmType,
        Long userId,
        String message,
        String targetToken
) { }
