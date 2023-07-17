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

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        ErrorCode errorCode = AuthenticationErrorCode.BAD_CREDENTIAL;
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> AppExceptionHandler(AppException e){
        return handleExceptionInternal(e.getErrorCode());
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeResponse(errorCode));
    }

    private Response<?> makeResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
        return Response.error(errorResponse);
    }
}
