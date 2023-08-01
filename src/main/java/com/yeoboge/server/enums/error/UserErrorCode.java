package com.yeoboge.server.enums.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "File upload error occurred in S3");
    private final HttpStatus httpStatus;
    private final String message;
}