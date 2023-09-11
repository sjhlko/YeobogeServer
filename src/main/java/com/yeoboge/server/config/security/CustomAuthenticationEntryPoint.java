package com.yeoboge.server.config.security;

import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.helper.utils.JsonResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import java.io.IOException;

/**
 * Http 404 에러에 대해 Spring Security에서 자체적으로
 * Http 403 에러를 응답하는 것을 처리하기 위한 {@link org.springframework.security.web.AuthenticationEntryPoint}
 */
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    private final HttpEndpointChecker endpointChecker;

    /**
     * Controller에서 핸들링하지 않는 엔드포인트에 대해 Http 404 에러로 변환함.
     *
     * @see JsonResponseUtils
     *
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     * @throws IOException
     */
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
