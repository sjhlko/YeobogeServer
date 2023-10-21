package com.yeoboge.server.enums.pushAlarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushAlarmType {
    CHATTING("chatting", null, null),
    RATING("rating", "즐거운 플레이 되셨나요?🎲", "그룹 추천을 받아 플레이 하셨던 보드게임을 평가해주세요!"),
    FRIEND_REQUEST("friendRequest", "친구 요청 알림🎲"," 님으로부터 친구요청이 왔습니다!"),
    FRIEND_ACCEPT("friendAccept", "친구 요청 수락 알림🎲"," 님이 친구신청을 수락했습니다!");
    private String key;
    private String title;
    private String message;
}
