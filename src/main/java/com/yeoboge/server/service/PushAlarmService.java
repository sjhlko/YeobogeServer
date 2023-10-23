package com.yeoboge.server.service;

import com.yeoboge.server.enums.pushAlarm.PushAlarmType;

/**
 * 푸시 알림 관련 비즈니스 로직에 대한 메서드를 제공하는 Service
 */
public interface PushAlarmService {

    /**
     * 특정 시간의 딜레이 후 푸시 알림을 전송함
     *
     * @param currentUserId 현재 로그인한 회원의 id
     * @param targetUserId 푸시 알림을 받을 유저의 인덱스
     * @param message       푸시알림 메세지 (채팅이 아닌 경우 null)
     * @param pushAlarmType 푸시 알림의 타입
     * @param delay         푸시 알림 전송 시 가질 딜레이
     */
    void sendPushAlarm(
            Long currentUserId,
            Long targetUserId,
            String message,
            PushAlarmType pushAlarmType,
            Integer delay
    );
}
