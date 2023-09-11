package com.yeoboge.server.helper.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboge.server.domain.vo.response.ErrorResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.enums.error.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Spring Security 필터에서 발생하는 예외나 Security의 에러 핸들러에서 예외 처리하는 경우,
 * {@link com.yeoboge.server.handler.GlobalExceptionHandler}가 처리를 담당하지 않으므로
 * 직접 {@link HttpServletResponse}에 에러 응답을 담아 전달하기 위한 유틸 클래스
 */
public class JsonResponseUtils {
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    /**
     * {@link ErrorCode}에 따라 해당 에러 응답을 {@code JSON} 형태로
     * 변환해 {@code Http Response Body}에 담아 전달함.
     *
     * @param response 현재 요청의 {@link HttpServletResponse} 응답
     * @param errorCode 발생한 {@link ErrorCode}
     * @throws IOException
     */
    public static void writeHttpErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        ErrorResponse body = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Response.error(body));

        response.getWriter().write(json);
    }
}
