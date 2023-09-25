package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.helper.utils.WebClientUtils;
import com.yeoboge.server.repository.RecommendRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 개인 추천 중 외부 API를 요청해서 추천 목록을 생성하는 로직에서
 * 공통된 로직을 분리하기 위한 추상 클래스
 * @param <T> 외부 API 요청 시 요청 body를 매핑하기 위한 DTO 클래스 타입
 */
public abstract class RecommendedByML<T> extends RecommendedBySomethingBase {
    private WebClient client;
    protected T requestBody;

    RecommendedByML(RecommendRepository repository, RecommendTypes type, WebClient client) {
        super(repository, type);
        this.client = client;
    }

    /**
     * Spring WebClient를 통해 외부 API로 추천 결과를 요청함.
     *
     * @param responseClass 외부 API에서 반환되는 응답 Body에 대한 DTO 클래스
     * @param endpoint 요청할 외부 API의 엔드포인트 URI
     * @return API 응답 결과를 비동기로 처리하기 위한 {@link Mono}
     * @param <E> 외부 API의 응답 Body를 매핑하기 위한 DTO 클래스 타입
     */
    protected <E> Mono<E> getWebClientMono(Class<E> responseClass, String endpoint) {
        return WebClientUtils.post(client, responseClass, requestBody, endpoint);
    }
}
