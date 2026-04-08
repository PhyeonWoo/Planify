package com.saas.Planify.service.auth.impl;

import com.saas.Planify.mapper.auth.AuthMapper;
import com.saas.Planify.model.dto.auth.AuthDto;
import com.saas.Planify.redis.RedisTokenService;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthMapper authMapper;
    private final RedisTokenService redisTokenService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void signUp(AuthDto.SignUpRequest request) {
        if(authMapper.existByEmail(request.email())) {
            log.warn("중복");
        }

        Long memberNo = authMapper.lastInsertId();
        String encodePw = passwordEncoder.encode(request.pw());

        authMapper.insertMember(AuthDto.LoginCreateRequest.of(
                request.email(),
                encodePw,
                memberNo
        ));
    }

    @Override
    public void logout(String bearerToken) {
       String email = jwtProvider.getUserId(bearerToken);
       redisTokenService.deleteRefreshToken(email);

       long expiration = jwtProvider.getExpiration(bearerToken);

       if(expiration > 0) {
           redisTokenService.setBlackList(bearerToken, expiration);
       }
    }

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        AuthDto.LoginInfoResponse info = authMapper.findByEmail(request.email());

        if(info == null) {
            log.warn("Not Found");
        }

        if(!passwordEncoder.matches(request.pw(), info.pw())) {
            log.error("Not match");
        }

        String accessToken = jwtProvider.createAccessToken(
                info.email(),
                info.memberNo(),
                "ROLE_USER"
        );

        String refreshToken = jwtProvider.createRefreshToken(
                info.email(),
                info.memberNo()
        );

        long refreshTokenExpiration = 7L * 24 * 60 * 60 * 1000;

        redisTokenService.saveRefreshToken(
                info.email(),
                refreshToken,
                refreshTokenExpiration
        );
        return AuthDto.LoginResponse.of(accessToken, refreshToken);
    }

    @Override
    public AuthDto.LoginResponse reissue(String refreshToken) {

        if(!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Error");
        }

        String email = jwtProvider.getUserId(refreshToken);
        String saveRefresh = redisTokenService.getRefreshToken(email);

        if(saveRefresh == null || !saveRefresh.equals(refreshToken)) {
            throw new IllegalArgumentException("오류");
        }

        AuthDto.LoginInfoResponse response = authMapper.findByEmail(email);
        String newAccessToken = jwtProvider.createAccessToken(
                response.email(),
                response.memberNo(),
                "ROLE_USER"
        );

        String newRefreshToken = jwtProvider.createRefreshToken(
                response.email(),
                response.memberNo()
        );
        long refreshTokenExpiration = 7L * 24 * 60 * 60 * 1000;

        redisTokenService.saveRefreshToken(email, newRefreshToken, refreshTokenExpiration);
        return AuthDto.LoginResponse.of(newRefreshToken, newAccessToken);
    }
}
