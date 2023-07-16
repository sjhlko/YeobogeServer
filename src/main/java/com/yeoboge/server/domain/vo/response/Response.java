package com.yeoboge.server.domain.vo.response;

public record Response<T>(String resultCode, T result) {
    public static <T> Response<T> error(T result){
        return new Response<>("ERROR",result);
    }
    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }

}
