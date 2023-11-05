package com.yeoboge.server.domain.vo.chat;

import lombok.Builder;

@Builder
public record StompMessageResponse(
        String msg,
        String timeStamp,
        Long sender
){ }

