package com.yeoboge.server.domain.vo.pushAlarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Fcm 메세지 전송시 전달 될 정보들을 담은 VO Class
 *
 */
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

    /**
     * 푸시 알림 시 화면에 표시할 내용과,
     * 해당 푸시 알림 클릭시 리다이렉트 될 페이지에 대한 정보를 담은 VO
     */
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        /**
         * 푸시 알림의 종류 {@link com.yeoboge.server.enums.pushAlarm.PushAlarmType}
         */
        private String pushAlarmType;
        /**
         * 푸시 알림에 표시할 제목
         */
        private String title;
        /**
         * 푸시 알림에 표시할 내용
         */
        private String body;
        /**
         * 푸시 알림에 들어갈 이미지 링크
         */
        private String image;
        /**
         * 채팅 푸시 알림 클릭 시 리다이렉트 될 채팅방의 id
         */
        private String id;
        private String roomId;
    }
}
