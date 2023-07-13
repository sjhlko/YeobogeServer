package com.yeoboge.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboge.server.dto.request.LoginRequest;
import com.yeoboge.server.dto.response.LoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
//@RequiredArgsConstructor
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/auths/login", "POST");
    private boolean postOnly = true;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      JwtProviderImpl jwtProvider) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        setAuthenticationManager(authenticationManager);
        System.out.println("Initialize Custom Authentication Filter");
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request,
                                                @NonNull HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (!this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        log.info("Authentication attempt { " + username + " " + password + " }");

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        String username = authResult.getName();
        String accessToken = jwtProvider.generateAccessToken(username);
        String refreshToken = jwtProvider.generateRefreshToken(username);

        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);
        String json = objectMapper.writeValueAsString(loginResponse);

        log.info("Authentication successed " + json);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
        throws IOException, ServletException {
        log.info("Failed to process authentication process");
        log.info("Handling authentication failed");
    }
}
