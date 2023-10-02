package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.pushAlarm.ChattingPushAlarmRequest;

import java.io.IOException;

public interface PushAlarmService {
    void sendPushAlarmForChatting(ChattingPushAlarmRequest request) throws IOException, InterruptedException;

}
