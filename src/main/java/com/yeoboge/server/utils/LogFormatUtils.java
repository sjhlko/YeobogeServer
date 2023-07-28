package com.yeoboge.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFormatUtils {
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String DATE_FORMAT = "[dd/MM/yyyy:HH:mm:ss Z]";

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
