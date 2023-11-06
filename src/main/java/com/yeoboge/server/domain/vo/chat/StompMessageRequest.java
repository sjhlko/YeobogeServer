package com.yeoboge.server.domain.vo.chat;

import lombok.Builder;

@Builder
public record StompMessageRequest(
        String msg,
        String timeStamp,
        String accessToken
){ }

