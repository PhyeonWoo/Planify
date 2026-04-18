package com.saas.Planify.service;

import com.saas.Planify.mapper.auth.AuthMapper;
import com.saas.Planify.model.dto.auth.AuthDto;
import com.saas.Planify.redis.RedisTokenService;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock private AuthMapper authMapper;
    @Mock private RedisTokenService redisTokenService;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 성공 - 올바른 이메일/비밀번호 입력 시 토큰 반환")
    void login_success() {
        // given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@test.com", "pass1234!");
        AuthDto.LoginInfoResponse info = new AuthDto.LoginInfoResponse(
                "test@test.com", "encodedPw", 1L, 10L, "ROLE_USER"
        );

        given(authMapper.findByEmail("test@test.com")).willReturn(info);
        given(passwordEncoder.matches("pass1234!", "encodedPw")).willReturn(true);
        given(jwtProvider.createAccessToken("test@test.com", 1L, "ROLE_USER")).willReturn("accessToken");
        given(jwtProvider.createRefreshToken("test@test.com", 1L)).willReturn("refreshToken");

        // when
        AuthDto.LoginResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
        assertThat(response.grantType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_fail_email_not_found() {
        // given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("nobody@test.com", "pass1234!");
        given(authMapper.findByEmail("nobody@test.com")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 계정입니다.");
    }


    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrong_password() {
        // given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@test.com", "wrongPw!");
        AuthDto.LoginInfoResponse info = new AuthDto.LoginInfoResponse(
                "test@test.com", "encodedPw", 1L, 10L, "ROLE_USER"
        );

        given(authMapper.findByEmail("test@test.com")).willReturn(info);
        given(passwordEncoder.matches("wrongPw!", "encodedPw")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }


    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signUp_fail_duplicate_email() {
        // given
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "exist@test.com", "pass1234!", "닉네임", "USER", "홍길동", "01012345678"
        );
        given(authMapper.existByEmail("exist@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}
