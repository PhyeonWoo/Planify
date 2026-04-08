package com.saas.Planify.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final StringRedisTemplate stringRedisTemplate;

    // RefreshToken 저장
    public void saveRefreshToken(String id, String refreshToken, long durationMills) {
        stringRedisTemplate.opsForValue().set(
                "KEY: " + id,
                refreshToken,
                durationMills,
                TimeUnit.MILLISECONDS
        );
        log.info("Redis에 Refresh Token 저장 완료 - User: {}", id);
    }


    public String getRefreshToken(String id) {
        return stringRedisTemplate.opsForValue().get("KEY: " + id);
    }


    // logout
    public void deleteRefreshToken(String id) {
        stringRedisTemplate.delete("KEY: "+id);
        log.info("Redis Logout - User: {}", id);
    }

    // Accesstoken 로그아웃 시 블랙리스트 등록
    public void setBlackList(String accessToken, long remainingTime) {
        stringRedisTemplate.opsForValue().set(
                accessToken,
                "logout",
                remainingTime,
                TimeUnit.MILLISECONDS
        );
    }

    // 블랙리스트 검증
    public boolean isBlackList(String accessToken) {
        return stringRedisTemplate.hasKey(accessToken);
    }

}
