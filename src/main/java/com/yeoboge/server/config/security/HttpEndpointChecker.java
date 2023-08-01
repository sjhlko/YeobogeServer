package com.yeoboge.server.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * 현재 서버로 들어온 Http 요청을 처리하는
 * {@link DispatcherServlet} 핸들러가 존재하는 지 확인하는 Component
 */
@Component
@RequiredArgsConstructor
public class HttpEndpointChecker {
    private final DispatcherServlet servlet;

    /**
     * 전체 {@link DispatcherServlet} 핸들러에 대해
     * 현재 요청을 처리하는 핸들러가 존재하는 지 확인함.
     *
     * @param request 현재 Http 요청
     * @return 핸들러가 존재하면 true, 아니면 false
     */
    public boolean isEndpointExist(HttpServletRequest request) {
        for (HandlerMapping mapping : servlet.getHandlerMappings()) {
            try {
                HandlerExecutionChain executionChain = mapping.getHandler(request);
                if (executionChain != null && !(executionChain.getHandler() instanceof ResourceHttpRequestHandler))
                    return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}
