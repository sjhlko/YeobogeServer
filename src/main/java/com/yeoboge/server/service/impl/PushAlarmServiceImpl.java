package com.yeoboge.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.pushAlarm.FcmMessage;
import com.yeoboge.server.domain.vo.pushAlarm.PushAlarmRequest;
import com.yeoboge.server.enums.error.PushAlarmErrorCode;
import com.yeoboge.server.enums.pushAlarm.PushAlarmType;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.PushAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushAlarmServiceImpl implements PushAlarmService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/yeoboge-6b9a9/messages:send";
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @Override
    public void sendPushAlarm(PushAlarmRequest request){
        try {
            String message = makeMessage(request);
            HttpRequest fcmRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(message))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    fcmRequest,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
        } catch (Exception e){
            throw new AppException(PushAlarmErrorCode.SENDING_PUSH_ALARM_ERROR);
        }

    }

    private String getAccessToken() {
        try {
            String firebaseConfigPath = "/fcm-key.json";

            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e){
            throw new AppException(PushAlarmErrorCode.CAN_NOT_MAKE_ACCESS_TOKEN);
        }

    }

    private String makeMessage(PushAlarmRequest request){
        try {
            FcmMessage.Data data = makeDataForChatting(request);
            if(request.pushAlarmType()==PushAlarmType.FRIEND_REQUEST) data = makeDataForFriendRequest(request);
            FcmMessage fcmMessage = FcmMessage.builder()
                    .message(FcmMessage.Message.builder()
                            .token(request.targetToken())
                            .data(data)
                            .build()
                    )
                    .validate_only(false)
                    .build();

            return objectMapper.writeValueAsString(fcmMessage);
        } catch (Exception e){
            throw new AppException(PushAlarmErrorCode.SENDING_PUSH_ALARM_ERROR);
        }
    }

    private FcmMessage.Data makeDataForChatting(PushAlarmRequest request){
        User user = userRepository.getById(request.currentUserId());
        return FcmMessage.Data.builder()
                .pushAlarmType(PushAlarmType.CHATTING.getKey())
                .title(user.getNickname())
                .body(request.message())
                .image(user.getProfileImagePath())
                .id(user.getId().toString())
                .build();

    }

    private FcmMessage.Data makeDataForFriendRequest(PushAlarmRequest request){
        User user = userRepository.getById(request.currentUserId());
        return FcmMessage.Data.builder()
                .pushAlarmType(PushAlarmType.FRIEND_REQUEST.getKey())
                .title(PushAlarmType.FRIEND_REQUEST.getTitle())
                .body(user.getNickname() + PushAlarmType.FRIEND_REQUEST.getMessage())
                .image(user.getProfileImagePath())
                .build();

    }
}
