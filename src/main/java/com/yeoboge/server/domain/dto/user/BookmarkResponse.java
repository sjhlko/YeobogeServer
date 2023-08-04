package com.yeoboge.server.domain.dto.user;

import lombok.Builder;

@Builder
public record BookmarkResponse(long userId, long boardGameId) {
}
