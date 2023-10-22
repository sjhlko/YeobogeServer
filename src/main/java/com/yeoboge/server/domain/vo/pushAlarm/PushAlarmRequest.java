package com.yeoboge.server.domain.vo.pushAlarm;

import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import lombok.Builder;
/**
 * 푸시 알림 요청시 해당 푸시 알림에 대한 정보를 담은 VO
 *
 * @param  pushAlarmType 푸시 알림의 타입 {@link PushAlarmType}
 * @param currentUserId 로그인한 회원의 id
 * @param targetUserId 푸시 알림을 받을 회원의 id
 * @param message 푸시 알림 전송 시 전달될 메세지 (ex) 채팅 메세지)
 * @param targetToken 푸시 알림이 전송 될 기기의 fcm token
 */

@Builder
public record PushAlarmRequest(
        PushAlarmType pushAlarmType,
        Long currentUserId,
        Long targetUserId,
        String message,
        String targetToken
) { }
