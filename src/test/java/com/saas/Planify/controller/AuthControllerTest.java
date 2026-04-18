package com.saas.Planify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.Planify.config.SecurityConfig;
import com.saas.Planify.controller.auth.AuthController;
import com.saas.Planify.model.dto.auth.AuthDto;
import com.saas.Planify.redis.RedisTokenService;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=hello1234hello1234hello1234hello1234hello1234hello1234hello12341515123124")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean AuthService authService;
    @MockitoBean JwtProvider jwtProvider;
    @MockitoBean RedisTokenService redisTokenService;

    @Test
    @DisplayName("로그인 API 성공 - 200 응답과 토큰 반환")
    void login_api_success() throws Exception {
        // given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@test.com", "pass1234!");
        AuthDto.LoginResponse loginResponse = AuthDto.LoginResponse.of("accessToken", "refreshToken");

        given(authService.login(any())).willReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.grantType").value("Bearer"));
    }

    @Test
    @DisplayName("로그인 API 실패 - 이메일 형식 오류 시 400 응답")
    void login_api_fail_invalid_email() throws Exception {
        // given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("not-an-email", "pass1234!");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 API 실패 - 비밀번호 형식 오류 시 400 응답")
    void signUp_api_fail_invalid_password() throws Exception {
        // given - 특수문자 없는 비밀번호
        AuthDto.SignUpRequest request = new AuthDto.SignUpRequest(
                "test@test.com", "password1", "닉네임", "USER", "홍길동", "01012345678"
        );

        // when & then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
