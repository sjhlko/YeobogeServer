package com.yeoboge.server.config.security.filter;

import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.utils.JsonResponseUtils;
import com.yeoboge.server.utils.LogFormatUtils;
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

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtException exception) {
            JsonResponseUtils.writeHttpErrorResponse(response, AuthenticationErrorCode.TOKEN_INVALID);
            log.info(LogFormatUtils.getHttpLog(request, response));
        }
    }
}
