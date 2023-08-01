package com.yeoboge.server.config.security.filter;

import com.yeoboge.server.config.security.HttpEndpointChecker;
import com.yeoboge.server.config.security.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * HTTP 요청의 Authrorization 헤더에 포함된 JWT 토큰을 통해
 * 사용자를 인증하기 위한 Spring Security Filter
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final String[] SKIP_AUTHENTICATION_URI = {
            "/auths/register",
            "/auths/email-duplicate",
            "/auths/login",
            "/auths/refresh",
            "/auths/temp-password"
    };
    private static final String HEADER_PREFIX = "Bearer ";
    private static final int TOKEN_SPLIT_INDEX = 7;

    private final HttpEndpointChecker endpointChecker;
    private final JwtProvider jwtProvider;

    /**
     * 현재 Http 요청이 JWT 인증이 필요한 요청인 경우
     * 헤더에 포함된 토큰으로부터 {@link com.yeoboge.server.domain.entity.User} ID를 파싱하고
     * 이를 통해 Spring Security 토큰 인증을 수행함.
     *
     * @see UsernamePasswordAuthenticationToken
     * @see SecurityContextHolder
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
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (canSkipFilter(request, header)) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.substring(TOKEN_SPLIT_INDEX);
        long userId = jwtProvider.parseUserId(token);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        if (jwtProvider.isValid(token, userId)) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);
        }

        chain.doFilter(request, response);
    }

    /**
     * 현재 요청이 JWT 인증이 필요하지 않은 요청인 지 확인함.
     *
     * @param request 현재 HTTP request
     * @param header Authorization 헤더
     * @return 인증이 필요하지 않은 요청이면 true, 아니면 false
     */
    private boolean canSkipFilter(HttpServletRequest request, String header) {
        return isNonAuthenticatedUri(request.getRequestURI()) ||
                !endpointChecker.isEndpointExist(request) ||
                header == null || !header.startsWith(HEADER_PREFIX);
    }

    /**
     * 현재 요청의 엔드포인트가 인증이 필요하지 않은 API인지 확인함.
     *
     * @param uri 현재 요청의 엔드포인트
     * @return 인증이 필요하지 않으면 true, 아니면 false
     */
    private boolean isNonAuthenticatedUri(String uri) {
        return Set.of(SKIP_AUTHENTICATION_URI).contains(uri);
    }
}
