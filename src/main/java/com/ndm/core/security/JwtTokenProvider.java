package com.ndm.core.security;

import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import com.ndm.core.model.exception.NotAuthenticatedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    @Value("${jwt.expiration.access}")
    private Long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private Long refreshTokenExpiration;

    @Value("${jwt.grant}")
    private String grant;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


        return TokenInfo.builder()
                .grantType(grant)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메소드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new NotAuthenticatedException(NO_AUTH_INFO_TOKEN);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public void validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new NotAuthenticatedException(INVALID_TOKEN, e);
        } catch (ExpiredJwtException e) {
            throw new NotAuthenticatedException(ACCESS_TOKEN_EXPIRED, e);
        } catch (UnsupportedJwtException e) {
            throw new NotAuthenticatedException(UNSUPPORTED_JWT, e);
        } catch (IllegalArgumentException e) {
            throw new NotAuthenticatedException(EMPTY_CLAIMS, e);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
        }
        catch (ExpiredJwtException e) {
            throw new NotAuthenticatedException(ACCESS_TOKEN_EXPIRED, e);
        }
        catch (Throwable e) {
            // ignore except ExpiredJwtException
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
