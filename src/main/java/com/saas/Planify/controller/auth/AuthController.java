package com.saas.Planify.controller.auth;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.auth.AuthDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ApiResponse<String> signUp(
            @Valid @RequestBody AuthDto.SignUpRequest request
    ) {
        log.info("start");
        authService.signUp(request);
        return ApiResponse.ok("생성 완료");
    }


    @PostMapping("/login")
    public ApiResponse<AuthDto.LoginResponse> login(
           @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        AuthDto.LoginResponse response = authService.login(request);
        return ApiResponse.ok(response);
    }



    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String accessToken = jwtProvider.resolveToken(bearerToken);
        authService.logout(accessToken);
        return ApiResponse.ok("로그아웃 완료");
    }


    @PostMapping("/reissue")
    public ApiResponse<AuthDto.LoginResponse> tokenReissue(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String refreshToken = jwtProvider.resolveToken(bearerToken);
        AuthDto.LoginResponse response = authService.reissue(refreshToken);
        return ApiResponse.ok(response);
    }



}
