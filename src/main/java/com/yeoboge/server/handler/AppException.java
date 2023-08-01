package com.yeoboge.server.handler;

import com.yeoboge.server.enums.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    public AppException(ErrorCode code) {
        this.errorCode = code;
        this.message = code.getMessage();
    }
}
