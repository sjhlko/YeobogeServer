package com.yeoboge.server.domain.vo.pushAlarm;

import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import lombok.Builder;

@Builder
public record PushAlarmRequestForChatting(
        PushAlarmType pushAlarmType,
        Long currentUserId,
        Long targetUserId,
        Long chatRoomId,
        String message,
        String targetToken
) {
}
