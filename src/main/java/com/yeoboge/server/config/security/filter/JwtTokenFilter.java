package com.yeoboge.server.config.security.filter;

import com.yeoboge.server.config.security.HttpEndpointChecker;
import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.vo.auth.Tokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    private boolean canSkipFilter(HttpServletRequest request, String header) {
        return isNonAuthenticatedUri(request.getRequestURI()) ||
                !endpointChecker.isEndpointExist(request) ||
                header == null || !header.startsWith(HEADER_PREFIX);
    }

    private boolean isNonAuthenticatedUri(String uri) {
        return Set.of(SKIP_AUTHENTICATION_URI).contains(uri);
    }
}
