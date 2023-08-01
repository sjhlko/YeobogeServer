package com.yeoboge.server.handler;

import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.error.ErrorCode;
import com.yeoboge.server.domain.vo.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller 단의 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Spring Security 인증 과정에서 발생한 예외를 처리함.
     *
     * @param exception {@link AuthenticationException}
     * @return 인증 관련 HTTP 에러 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        ErrorCode errorCode = AuthenticationErrorCode.BAD_CREDENTIAL;
        return handleExceptionInternal(errorCode);
    }

    /**
     * 서버에서 발생한 자체 예외를 처리함.
     *
     * @param e {@link AppException}
     * @return HTTP 에러 응답
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> AppExceptionHandler(AppException e){
        return handleExceptionInternal(e.getErrorCode());
    }

    /**
     * HTTP 에러 응답 객체를 반환함.
     *
     * @param errorCode 발생한 예외에 대한 {@link ErrorCode}
     * @return {@link ResponseEntity}
     */
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeResponse(errorCode));
    }

    /**
     * 에러 관련 자체 응답 객체를 반환함.
     *
     * @param errorCode 발생한 예외에 대한 {@link ErrorCode}
     * @return {@link Response}
     */
    private Response<?> makeResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
        return Response.error(errorResponse);
    }
}
