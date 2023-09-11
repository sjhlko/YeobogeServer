package com.yeoboge.server.helper.utils;

import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient를 통해 외부 API 요청을 처리하는 유틸 클래스
 */
public class WebClientUtils {
    /**
     * 외부 API로 GET 요청을 비동기로 보낸 후 응답의 Response Body를 {@link Mono} 형태로 반환함.
     *
     * @param client {@link WebClient}
     * @param responseDtoClass 요청한 API에서 응답하는 Response Body와 매핑할 클래스 타입
     * @param endpoint 요청할 API 엔드포인트 형식. {@code PathVariable}이 필요한 경우, 해당 형식에 맞춰서 전달
     * @param variables 엔드포인트에 {@code PathVariable}이 필요한 경우, 해당 값에 넘겨주기 위한 value 값
     * @return 해당 API 응답의 Response Body에 해당하는 정보를 {@code T} 형태로 매핑한 {@link Mono} 객체
     * @param <T> API에서 응답으로 넘겨주는 Body와 매핑할 클래스 타입
     */
    public static <T> Mono<T> get(WebClient client, Class<T> responseDtoClass, String endpoint, Object ...variables) {
        return client.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path(endpoint).build(variables))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new AppException(CommonErrorCode.NOT_FOUND))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR))
                )
                .bodyToMono(responseDtoClass);
    }
}
