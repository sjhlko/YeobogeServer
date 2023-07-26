package com.yeoboge.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboge.server.domain.vo.response.ErrorResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.enums.error.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonResponseUtils {
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

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
