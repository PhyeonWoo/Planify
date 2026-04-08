package com.saas.Planify.model.dto.auth;

public class AuthDto {

    // id DB
    public record SignUpRequest(
            String email, // email
            String pw, // pw
            String nickName, // nick
            String role, // role
            String name,
            String phoneNumber// name
    ) {}

    // Login 생성 요청
    public record LoginCreateRequest(
            String email,
            String pw,
            Long memberNo
    ) {
        public static LoginCreateRequest of(String email, String encodePw, Long memberNo) {
            return new LoginCreateRequest(email, encodePw, memberNo);
        }
    }

    //Login 시도
    public record LoginRequest(
            String email,
            String pw
    ){}

    // Login 성공 시 응답 값
    public record LoginResponse(
            String grantType,
            String accessToken,
            String refreshToken
    ) {
        public static LoginResponse of(String accessToken, String refreshToken) {
            return new LoginResponse("Bearer ",accessToken, refreshToken);
        }
    }


    // Login 정보 응답 (서버용 DTO)
    public record LoginInfoResponse(
            String email,
            String pw,
            Long memberNo
    ) {}

}
