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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private static final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000;


    private final AuthMapper authMapper;
    private final RedisTokenService redisTokenService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;



    @Override
    @Transactional
    public void signUp(AuthDto.SignUpRequest request) {
        log.info("Reqeust Start");
        if (authMapper.existByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        authMapper.insertMember(AuthDto.MemberCreateRequest.from(request));
//        AuthDto.MemberCreateRequest memberReq = AuthDto.MemberCreateRequest.from(request);
//        authMapper.insertMember(memberReq);

        Long memberNo = authMapper.lastInsertId();
        String encodePw = passwordEncoder.encode(request.pw());

        authMapper.insertLogin(AuthDto.LoginCreateRequest.of(
                request.email(),
                encodePw,
                memberNo));
    }

    @Override
    public void logout(String bearerToken) {
        String email = jwtProvider.getUserId(bearerToken);

        redisTokenService.deleteRefreshToken(email);
        long expiration = jwtProvider.getExpiration(bearerToken);

        if (expiration > 0) {
            redisTokenService.setBlackList(bearerToken, expiration);
        }

    }

//    @Override
//    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
//        AuthDto.LoginInfoResponse info = authMapper.findByEmail(request.email());
//
//        if (info == null) {
//            throw new IllegalArgumentException("존재하지 않는 계정입니다.");
//        }
//
//        if (!passwordEncoder.matches(request.pw(), info.pw())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//
////// AuthService의 로그인 메서드 안에서
////        Member member = memberMapper.findByEmail(request.getEmail()).get();
//
//// 여기서 member.getMemberNo()가 '3'을 가져오는지 확인!
//        String token = jwtProvider.createAccessToken(
//                info.email(),
//                info.memberNo(), // 이 값이 1이 아니라 3이어야 합니다.
//                info.()
//        );
//        String refreshToken = jwtProvider.createRefreshToken(info.email(), info.loginNo());
//
//        redisTokenService.saveRefreshToken(info.email(), refreshToken, REFRESH_TOKEN_EXPIRATION);
//
//        return AuthDto.LoginResponse.of(accessToken, refreshToken);
//    }


@Override
public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
    AuthDto.LoginInfoResponse info = authMapper.findByEmail(request.email());

    if (info == null) {
        throw new IllegalArgumentException("존재하지 않는 계정입니다.");
    }

    if (!passwordEncoder.matches(request.pw(), info.pw())) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    log.info("로그인 시도 memberNo: {}", info.memberNo());

    String accessToken = jwtProvider.createAccessToken(
            info.email(),
            info.memberNo(),
            "ROLE_USER"
    );

    String refreshToken = jwtProvider.createRefreshToken(info.email(), info.memberNo());

    redisTokenService.saveRefreshToken(info.email(), refreshToken, REFRESH_TOKEN_EXPIRATION);

    return AuthDto.LoginResponse.of(accessToken, refreshToken);
}

    @Override
    public AuthDto.LoginResponse reissue(String refreshToken) {
        log.info("입력받은 Refresh Token: [{}]", refreshToken);

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtProvider.getUserId(refreshToken);
        String savedRefresh = redisTokenService.getRefreshToken(email);

        if (savedRefresh == null || !savedRefresh.equals(refreshToken)) {
            throw new IllegalArgumentException("저장된 토큰과 일치하지 않습니다.");
        }

        AuthDto.LoginInfoResponse info = authMapper.findByEmail(email);
        String newAccessToken = jwtProvider.createAccessToken(info.email(), info.memberNo(), "ROLE_USER");
        String newRefreshToken = jwtProvider.createRefreshToken(info.email(), info.memberNo());

        redisTokenService.saveRefreshToken(email, newRefreshToken, REFRESH_TOKEN_EXPIRATION);

        // 순서: accessToken, refreshToken
        return AuthDto.LoginResponse.of(newAccessToken, newRefreshToken);
    }
}