package com.yeoboge.server.enums.error;

import org.springframework.http.HttpStatus;

/**
 * Http 에러 응답 시 전달할 Error Code의 Getter 메서드를 제공하는 인터페이스
 */
public interface ErrorCode {
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
