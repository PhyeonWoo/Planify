package com.saas.Planify.service.auth;

import com.saas.Planify.model.dto.auth.AuthDto;

public interface AuthService {

    void signUp(AuthDto.SignUpRequest request);

    void logout(String bearerToken);

    // 로그인 시도
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);

    // 토큰 재발급
    AuthDto.LoginResponse reissue(String refreshToken);
}
