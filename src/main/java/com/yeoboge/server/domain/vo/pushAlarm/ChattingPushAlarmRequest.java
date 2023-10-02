package com.yeoboge.server.domain.vo.pushAlarm;

import lombok.Builder;

@Builder
public record ChattingPushAlarmRequest(Long userId, String message, String targetToken) { }
