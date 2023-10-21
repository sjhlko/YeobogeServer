package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;

/**
 * 푸시 알림 관련 비즈니스 로직에 대한 메서드를 제공하는 Service
 */
public interface PushAlarmService {

    /**
     * 특정 시간의 딜레이 후 푸시 알림을 전송함
     *
     * @param request 푸시 알림 전송에 관련한 정보를 담은 {@link PushAlarmRequest} VO
     * @param delay 푸시 알림 전송 시 가질 딜레이
     */
    void sendPushAlarm(PushAlarmRequest request, Integer delay);
}
