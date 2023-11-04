package com.yeoboge.server.config;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HandShakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static final int TOKEN_SPLIT_INDEX = 7;
    private final JwtProvider jwtProvider;
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        String authorizationHeader = String.valueOf(request.getHeaders().get("Authorization"));
//        if (authorizationHeader == null || authorizationHeader.equals("[]")){
//            response.setStatusCode(AuthenticationErrorCode.TOKEN_INVALID.getHttpStatus());
//            return false;
//        }
//
//        String accessToken = authorizationHeader.substring(TOKEN_SPLIT_INDEX);
//        long userId = jwtProvider.parseUserId(accessToken);
//        if (!jwtProvider.isValid(accessToken, userId)){
//            response.setStatusCode(AuthenticationErrorCode.TOKEN_INVALID.getHttpStatus());
//            return false;
//        }
//
//        return true;
//    }

//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
//        response.g
//        super.afterHandshake(request, response, wsHandler, ex);
//    }
}
