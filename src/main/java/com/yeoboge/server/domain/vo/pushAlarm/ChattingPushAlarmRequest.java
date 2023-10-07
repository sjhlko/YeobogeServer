package com.yeoboge.server.domain.vo.pushAlarm;

import lombok.Builder;

@Builder
public record ChattingPushAlarmRequest(Long currentUserId, String message, String targetToken) { }
