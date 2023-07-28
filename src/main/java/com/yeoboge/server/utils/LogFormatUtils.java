package com.yeoboge.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogFormatUtils {
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String DATE_FORMAT = "[dd/MM/yyyy:HH:mm:ss Z]";
    private static final String[] REMOTE_HOST_HEADERS = {
            "X-Forwarded-For",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_VIA",
            "IPV6_ADR"
    };

    public static String getHttpLog(HttpServletRequest request, HttpServletResponse response) {
        return getRemoteAddr(request) + " "
                + getRemoteUser() + " "
                + getDateTime() + " \""
                + request.getMethod() + " "
                + request.getRequestURI() + " "
                + request.getProtocol() + "\" "
                + response.getStatus();
    }

    private static String getRemoteAddr(HttpServletRequest request) {
        List<String> remoteHostHeaders = List.of(REMOTE_HOST_HEADERS);

        for (String header : remoteHostHeaders) {
            String clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !clientIp.equals("unknown"))
                return clientIp;
        }

        return request.getRemoteAddr();
    }

    private static String getRemoteUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? String.valueOf(auth.getPrincipal()) : ANONYMOUS_USER;
    }

    private static String getDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        return dateFormat.format(now);
    }
}
