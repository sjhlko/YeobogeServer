package com.yeoboge.server.config.security.filter;

import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.error.ErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.helper.utils.JsonResponseUtils;
import com.yeoboge.server.helper.utils.LogFormatUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>{@link JwtTokenFilter}에서 JWT 토큰을 파싱하는 과정에 발생한
 * Exception을 처리하기 위한 Security Filter
 *
 * <p>{@link JwtTokenFilter} 이전 순서로 {@link FilterChain}에 등록되어
 * 해당 필터에서 토큰 관련 예외가 발생했을 시에 요청을 Servlet으로 전달하지 않고
 * 현재 필터에서 예외 처리 후 Http 응답을 바로 전달함.
 *
 * @author Yoon Soobin
 */
@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    /**
     * <p>chain에 연결된 다음 필터인 {@link JwtTokenFilter}를 실행한 뒤,
     * {@link JwtException}이 발생한 경우 잘못된 토큰 에러를 응답으로 반환함.
     * 현재 필터에서 응답을 바로 전달하는 것이므로 요청이 chain에 등록된 나머지 필터는 거치지 않아
     * 별도의 로깅 처리를 함.
     *
     * @see LogFormatUtils
     * @see AuthenticationErrorCode
     *
     * @param request 현재 Http request
     * @param response 현 요청에 대한 Http response
     * @param chain 전체 Filter가 등록된 Security Filter Chain
     * @throws ServletException I/O 외 다른 이유로 인해 요청 처리를 실패한 경우
     * @throws IOException 요청을 처리하는 중 I/O 에러가 발생한 경우
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtException exception) {
            writeLog(request, response, AuthenticationErrorCode.TOKEN_INVALID);
        } catch (AppException exception) {
            if (exception.getErrorCode() == AuthenticationErrorCode.TOKEN_INVALID)
                writeLog(request, response, AuthenticationErrorCode.TOKEN_INVALID);
        }
    }

    private void writeLog(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode cord
    ) throws IOException {
        JsonResponseUtils.writeHttpErrorResponse(response, AuthenticationErrorCode.TOKEN_INVALID);
        log.info(LogFormatUtils.getHttpLog(request, response));
    }
}
