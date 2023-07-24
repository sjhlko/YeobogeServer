package com.yeoboge.server.domain.vo.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Response<T>(String resultCode, T result) {
    public static <T> Response<T> error(T result){
        return new Response<>("ERROR",result);
    }

    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }

    public static <T> ResponseEntity<Response<T>> created(T result) {
        Response<T> response = new Response<>("CREATED", result);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
