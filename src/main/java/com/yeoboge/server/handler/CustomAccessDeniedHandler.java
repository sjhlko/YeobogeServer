package com.yeoboge.server.handler;

import com.yeoboge.server.config.security.HttpEndpointChecker;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.utils.JsonResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
    private final HttpEndpointChecker endpointChecker;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception)
            throws IOException, ServletException {
        if (!endpointChecker.isEndpointExist(request)) {
            JsonResponseUtils.writeHttpErrorResponse(response, CommonErrorCode.NOT_FOUND);
        } else {
            super.handle(request, response, exception);
        }
    }
}
