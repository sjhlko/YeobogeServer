package com.yeoboge.server.config.security;

import com.yeoboge.server.domain.vo.auth.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtProviderImpl implements JwtProvider {
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.sign.key}")
    private String key;

    @Override
    public Tokens generateTokens(Long userId) {
        return Tokens.builder()
                .accessToken(generateAccessToken(userId))
                .refreshToken(generateRefreshToken(userId))
                .build();
    }

    @Override
    public boolean isValid(String token, Long userId) {
        final Long extractedUserId = parseUserId(token);
        return userId == extractedUserId && !isTokenExpired(token);
    }

    @Override
    public Long parseUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    private String generateAccessToken(Long userId) {
        return generateToken(new HashMap<>(), userId, ACCESS_TOKEN_EXPIRED_TIME);
    }

    private String generateRefreshToken(Long userId) {
        return generateToken(new HashMap<>(), userId, REFRESH_TOKEN_EXPIRED_TIME);
    }

    private String generateToken(Map<String, Object> extraClaims, Long userId, long expiredTime) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiredTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
