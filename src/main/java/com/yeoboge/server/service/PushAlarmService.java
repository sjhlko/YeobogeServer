package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;

public interface PushAlarmService {
    void sendPushAlarm(PushAlarmRequest request);

}
