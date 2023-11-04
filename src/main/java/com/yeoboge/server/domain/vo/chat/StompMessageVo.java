package com.yeoboge.server.domain.vo.chat;

import lombok.Builder;
import lombok.Setter;

@Builder
public record StompMessageVo(
        String msg,
        String timeStamp,
        Long sender
){ }

