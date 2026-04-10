package com.saas.Planify.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtProvider {

    private String secret = "hello1234hello1234hello1234hello1234hello1234hello1234hello12341515123124";
    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String id, Long memberNo, String role, Long time) {
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("memberNo",memberNo);

        if(role != null) {
            claims.put("role",role);
        }


        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(String userId, Long memberNo, String role) {
        return createToken(userId, memberNo, role, 30 * 60 * 1000L);
    }

    public String createRefreshToken(String userId, Long memberNo) {
        return createToken(userId, memberNo, null, 7 * 24 * 60 * 60 * 1000L);
    }

    // Token -> userId
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    //Token -> memberNo
    public Long getMemberNo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("memberNo", Long.class);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    // http 요청 헤더에서 토큰 추출
    public String resolveToken(String bearerToken) {
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public Authentication getAuthentication(String token) {
        String id = getUserId(token);
        return new UsernamePasswordAuthenticationToken(id,"",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public long getExpiration(String token) {
        try{
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getExpiration();

            long now = new Date().getTime();
            return expiration.getTime() - now;
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }

}
