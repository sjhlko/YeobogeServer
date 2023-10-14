package com.yeoboge.server.domain.vo.pushAlarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token;
        private Data data;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String pushAlarmType;
        private String title;
        private String body;
        private String image;
        private String id;
    }
}
