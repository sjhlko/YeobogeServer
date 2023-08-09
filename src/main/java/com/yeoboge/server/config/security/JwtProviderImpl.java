package com.yeoboge.server.config.security;

import com.yeoboge.server.domain.vo.auth.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * {@link JwtProvider} 구현체
 */
@Component
public class JwtProviderImpl implements JwtProvider {
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 7;
    private static final SecretKey SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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

    /**
     * 사용자 ID를 페이로드에 담아 Access Token을 발급함.
     *
     * @param userId 페이로드에 담을 {@link com.yeoboge.server.domain.entity.User} ID
     * @return 발급된 Access Token
     */
    private String generateAccessToken(Long userId) {
        return generateToken(new HashMap<>(), userId, ACCESS_TOKEN_EXPIRED_TIME);
    }

    /**
     * 사용자 ID를 페이로드에 담아 Refresh Token을 발급함.
     *
     * @param userId 페이로드에 담을 {@link com.yeoboge.server.domain.entity.User} ID
     * @return 발급된 Refresh Token
     */
    private String generateRefreshToken(Long userId) {
        return generateToken(new HashMap<>(), userId, REFRESH_TOKEN_EXPIRED_TIME);
    }

    /**
     * 사용자 ID를 페이로드에 담고 만료 시간을 설정해 JWT를 발급함.
     *
     * @param extraClaims 토큰에 추가할 별도의 JSON Claim
     * @param userId {@link com.yeoboge.server.domain.entity.User} ID
     * @param expiredTime 토큰 만료 시간
     * @return 발급된 Json Web Token
     */
    private String generateToken(Map<String, Object> extraClaims, Long userId, long expiredTime) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiredTime))
                .signWith(SIGNING_KEY)
                .compact();
    }

    /**
     * 토큰의 만료 여부를 검증함.
     *
     * @param token 검증할 토큰
     * @return 토큰 만료 시각이 지났으면 true, 아니면 false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * 토큰의 만료 시각을 추출함.
     *
     * @param token 만료 시각을 확인할 토큰
     * @return 토큰의 만료 시각
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
