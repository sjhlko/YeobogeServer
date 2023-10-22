package com.yeoboge.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.pushAlarm.FcmMessage;
import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;
import com.yeoboge.server.enums.error.PushAlarmErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.TokenRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.PushAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * {@link PushAlarmService} 구현체
 */
@Service
@RequiredArgsConstructor
@EnableScheduling
public class PushAlarmServiceImpl implements PushAlarmService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/yeoboge-6b9a9/messages:send";
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TaskScheduler taskScheduler;

    @Override
    public void sendPushAlarm(Long currentUserId, Long targetUserId, String message,
                              PushAlarmType pushAlarmType, Integer delay) {
        taskScheduler.schedule(() -> {
            try {
                PushAlarmRequest request = makePushAlarmRequest(currentUserId, targetUserId, message, pushAlarmType);
                if (request == null) return;

                String fcmMessage = makeMessage(request);
                HttpRequest fcmRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .method("POST", HttpRequest.BodyPublishers.ofString(fcmMessage))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(
                        fcmRequest,
                        HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (Exception e) {
                throw new AppException(PushAlarmErrorCode.SENDING_PUSH_ALARM_ERROR);
            }
        }, new Date(System.currentTimeMillis() + delay));
    }

    /**
     * fcm token을 이용하여 푸시 알림 전송 시 필요한 access token을 발급/재발급함
     *
     * @return access token
     * @throws AppException 토큰 생성 실패에 관한 error를 던짐
     */
    private String getAccessToken() {
        try {
            String firebaseConfigPath = "/fcm-key.json";
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new AppException(PushAlarmErrorCode.CAN_NOT_MAKE_ACCESS_TOKEN);
        }

    }

    /**
     * 푸시 알림 전송 시 전달 될 message를 만들어 리턴함
     *
     * @param request 푸시 알림으로 전달 될 메세지에 대한 내용을 담은 {@link PushAlarmRequest} VO
     * @return 푸시 알림 전송 시 전달 될 메세지
     * @throws AppException 푸시 알림 전송 실패에 관한 에러를 던짐
     */
    private String makeMessage(PushAlarmRequest request) {
        try {
            FcmMessage.Data data = null;
            if (request.pushAlarmType() == PushAlarmType.CHATTING) data = makeDataForChatting(request);
            if (request.pushAlarmType() == PushAlarmType.FRIEND_REQUEST
                    || request.pushAlarmType() == PushAlarmType.FRIEND_ACCEPT)
                data = makeDataForFriend(request);
            if (request.pushAlarmType() == PushAlarmType.RATING) data = makeDataForGroupRecommendation();
            FcmMessage fcmMessage = FcmMessage.builder()
                    .message(FcmMessage.Message.builder()
                            .token(request.targetToken())
                            .data(data)
                            .build()
                    )
                    .validate_only(false)
                    .build();

            return objectMapper.writeValueAsString(fcmMessage);
        } catch (Exception e) {
            throw new AppException(PushAlarmErrorCode.SENDING_PUSH_ALARM_ERROR);
        }
    }

    /**
     * 푸시 알림 전송 시 사용될 데이터인 {@link PushAlarmRequest} VO를 만들어 리턴함
     *
     * @param currentUserId 현재 로그인 중인 유저의 id
     * @param targetUserId  푸시 알림을 받을 유저의 id
     * @param message       푸시 알림의 내용 (채팅이 아닐 경우 null)
     * @param pushAlarmType 푸시 알람의 타입 {@link PushAlarmType}
     * @return {@link PushAlarmRequest}
     */
    private PushAlarmRequest makePushAlarmRequest(Long currentUserId, Long targetUserId,
                                                  String message, PushAlarmType pushAlarmType) {
        Optional<String> fcmToken = tokenRepository.findFcmToken(targetUserId);
        return fcmToken.map(s -> PushAlarmRequest.builder()
                .pushAlarmType(pushAlarmType)
                .targetToken(s)
                .message(message)
                .currentUserId(currentUserId)
                .targetUserId(targetUserId)
                .build()).orElse(null);
    }

    /**
     * 채팅 관련 푸시 알림 전송 시 전달 될 message의 data 부분을 만들어 리턴함
     *
     * @param request 푸시 알림으로 전달 될 메세지에 대한 내용을 담은 {@link PushAlarmRequest} VO
     * @return 푸시 알림 전송 시 전달 될 메세지의 data
     */
    private FcmMessage.Data makeDataForChatting(PushAlarmRequest request) {
        User user = userRepository.getById(request.currentUserId());
        return FcmMessage.Data.builder()
                .pushAlarmType(request.pushAlarmType().getKey())
                .title(user.getNickname())
                .body(request.message())
                .image(user.getProfileImagePath())
                .id(user.getId().toString())
                .build();

    }

    /**
     * 친구 요청/수락 관련 푸시 알림 전송 시 전달 될 message의 data 부분을 만들어 리턴함
     *
     * @param request 푸시 알림으로 전달 될 메세지에 대한 내용을 담은 {@link PushAlarmRequest} VO
     * @return 푸시 알림 전송 시 전달 될 메세지의 data
     */
    private FcmMessage.Data makeDataForFriend(PushAlarmRequest request) {
        User user = userRepository.getById(request.currentUserId());
        return FcmMessage.Data.builder()
                .pushAlarmType(request.pushAlarmType().getKey())
                .title(request.pushAlarmType().getTitle())
                .body(user.getNickname() + request.pushAlarmType().getMessage())
                .image(user.getProfileImagePath())
                .build();

    }

    /**
     * 보드게임 추천 관련 푸시 알림 전송 시 전달 될 message의 data 부분을 만들어 리턴함
     *
     * @return 푸시 알림 전송 시 전달 될 메세지의 data
     */
    private FcmMessage.Data makeDataForGroupRecommendation() {
        return FcmMessage.Data.builder()
                .pushAlarmType(PushAlarmType.RATING.getKey())
                .title(PushAlarmType.RATING.getTitle())
                .body(PushAlarmType.RATING.getMessage())
                .build();
    }
}
