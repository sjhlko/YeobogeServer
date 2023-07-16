package com.yeoboge.server.security;

public interface JwtProvider {
    String generateAccessToken(String username);
    String generateRefreshToken(String username);
    boolean isValid(String token, String username);
    String parseUsername(String token);
}
