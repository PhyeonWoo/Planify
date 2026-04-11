package com.saas.Planify.security;

import com.saas.Planify.redis.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("======= JwtFilter 진입: {} =======", path);

        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/webjars") ||
                path.contains("/invites/") || // 초대 수락 경로 포함
                path.startsWith("/api/v1/auth/")) { // 로그인, 회원가입 등

            log.info("======= 인증 패스 경로: {} =======", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더 추출
        String authorization = request.getHeader("Authorization");
        log.info("======= 헤더 토큰: {} =======", authorization);

        // 토큰 파싱 및 검증
        try {
            String token = jwtProvider.resolveToken(authorization);

            if (token != null && jwtProvider.validateToken(token)) {
                if (redisTokenService.isBlackList(token)) {
                    log.warn("블랙리스트에 등록된 토큰입니다.");
                } else {
                    Authentication auth = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("======= 인증 완료: {} =======", auth.getName());
                }
            }
        } catch (Exception e) {
            // 초대 수락 API에서 토큰이 없을 때 여기서 예외가 터지면 안 됩니다.
            log.error("토큰 검증 중 에러 발생: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}