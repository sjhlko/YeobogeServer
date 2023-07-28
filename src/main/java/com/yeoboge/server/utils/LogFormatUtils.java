package com.yeoboge.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Http 요청 및 응답 관련 로그를 저장하기 위해 상세 정보를 포맷에 맞춰 생성 후 반환하는 Util 클래스
 *
 * @author Yoon Soobin
 */
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

    /**
     * <p>현재 요청과 응답에 대해 로깅할 정보를 문자열로 파싱하여 반환함.
     *
     * @param request 현재 Http request
     * @param response 현재 요청에 대한 Http response
     * @return 로깅할 데이터를 포맷에 맞게 변환한 문자열
     */
    public static String getHttpLog(HttpServletRequest request, HttpServletResponse response) {
        return getRemoteAddr(request) + " "
                + getRemoteUser() + " "
                + getDateTime() + " \""
                + request.getMethod() + " "
                + request.getRequestURI() + " "
                + request.getProtocol() + "\" "
                + response.getStatus();
    }

    /**
     * 요청을 보낸 Host의 IP 주소를 파싱하기 위해 Request Header에서 주소값을 가져옴
     *
     * @param request 현재 Http request
     * @return 클라이언트 IP 주소
     */
    private static String getRemoteAddr(HttpServletRequest request) {
        List<String> remoteHostHeaders = List.of(REMOTE_HOST_HEADERS);

        for (String header : remoteHostHeaders) {
            String clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !clientIp.equals("unknown"))
                return clientIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * <p>현재 요청을 보낸 사용자가 로그인한 사용자라면 해당 사용자의 ID를 반환하고,</p>
     * 로그인 정보가 없다면 익명의 사용자라는 디폴트 값을 반환함.
     *
     * @return 현재 요청을 보낸 사용자
     */
    private static String getRemoteUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? String.valueOf(auth.getPrincipal()) : ANONYMOUS_USER;
    }

    /**
     * 현재 요청을 보낸 시각을 포맷에 맞게 문자열로 변환하여 반환함.
     *
     * @return 요청을 보낸 시각
     */
    private static String getDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        return dateFormat.format(now);
    }
}
