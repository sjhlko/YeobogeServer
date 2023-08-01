package com.yeoboge.server.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
public class HttpEndpointChecker {
    private final DispatcherServlet servlet;

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
