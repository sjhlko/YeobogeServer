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

/**
 * Http 403 에러 핸들러
 */
@RequiredArgsConstructor
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
    private final HttpEndpointChecker endpointChecker;

    /**
     * 서버에서 발생하는 403 에러에 대해 Controller에 매핑되지 않는
     * 엔드포인트의 요청도 403 에러로 응답하는 것을 404 에러로 변환하여 응답함.
     *
     * @see JsonResponseUtils
     *
     * @param request that resulted in an <code>AccessDeniedException</code>
     * @param response so that the user agent can be advised of the failure
     * @param exception that caused the invocation
     * @throws IOException
     * @throws ServletException
     */
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
