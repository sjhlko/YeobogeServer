package com.yeoboge.server.config.security;

import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.utils.JsonResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    private final HttpEndpointChecker endpointChecker;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        if (!endpointChecker.isEndpointExist(request)) {
            JsonResponseUtils.writeHttpErrorResponse(response, CommonErrorCode.NOT_FOUND);
        } else {
            super.commence(request, response, authException);
        }
    }
}
