package com.yeoboge.server.domain.vo.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 클라이언트에 전달할 HTTP 공통 응답 VO
 *
 * @param resultCode
 * @param result HTTP 응답 바디
 * @param <T>
 */
public record Response<T>(String resultCode, T result) {
    /**
     * HTTP 에러 응답을 반환함.
     *
     * @param result {@link ErrorResponse}
     * @return 에러 응답
     * @param <T> 에러 Body의 클래스 타입
     */
    public static <T> Response<T> error(T result){
        return new Response<>("ERROR",result);
    }

    /**
     * HTTP 성공 응답을 반환함.
     *
     * @param result 응답에 포함될 Response Body
     * @return 성공 응답
     * @param <T> Response Body의 클래스 타임
     */
    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }

    /**
     * HTTP 201 응답을 반환함.
     *
     * @param result 응답에 포함될 Response Body
     * @return 201 응답
     * @param <T> Response Body의 클래스 타입
     */
    public static <T> ResponseEntity<Response<T>> created(T result) {
        Response<T> response = new Response<>("CREATED", result);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * HTTP 204 응답을 반환함.
     *
     * @return 204 응답
     */
    public static ResponseEntity deleted() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
