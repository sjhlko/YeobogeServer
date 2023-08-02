package com.yeoboge.server.config.security.filter;

import com.yeoboge.server.utils.LogFormatUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>API 엔드포인트로 들어오는 HTTP 요청에 대해
 * 해당 요청의 상세 정보와 응답 Http 상태 코드를 로깅하는 Security Filter
 *
 * @author Yoon Soobin
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    /**
     * <p>chain에 연결된 모든 필터를 처리하고 난 뒤, 현재 Http 요청에 대한 응답을 로깅함.
     *
     * @see LogFormatUtils
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
        chain.doFilter(request, response);
        log.info(LogFormatUtils.getHttpLog(request, response));
    }
}
