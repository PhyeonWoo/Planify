package com.saas.Planify.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDto {

    // login DB 생성부분
    public record SignUpRequest(
            @Email(message = "email 형식이 아닙니다.")
            String email,

            @NotBlank(message = "PW는 필수 입력입니다.")
            @Size(min = 5, message = "비밀번호는 5자 이상이어야 합니다.")
            @Pattern(
                    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]+$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
            )
            String pw,

            String nickName,

            @NotBlank(message = "빈칸이면 안됩니다.")
            String role,

            @NotBlank(message = "빈칸이면 안됩니다.")
            String name,

            @NotBlank(message = "빈칸이면 안됩니다.")
            @Pattern(regexp = "^\\d{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
            String phoneNumber
    ) {}

    // Member insert용 내부 DTO
    public record MemberCreateRequest(
            String name,
            String nickname,
            String phoneNumber,
            String role
    ) {
        public static MemberCreateRequest from(SignUpRequest req) {
            return new MemberCreateRequest(req.name(), req.nickName(), req.phoneNumber(), req.role());
        }
    }

    // 이게 메인
    public record LoginCreateRequest(
            String email,
            String pw,
            Long memberNo
    ) {
        public static LoginCreateRequest of(String email, String encodePw, Long memberNo) {
            return new LoginCreateRequest(email, encodePw, memberNo);
        }
    }


    public record LoginRequest(
            @Email(message = "email 형식이 아닙니다.")
            String email,
            @NotBlank(message = "빈칸이면 안됩니다.")
            String pw
    ) {}

    public record LoginResponse(
            String grantType,
            String accessToken,
            String refreshToken
    ) {
        public static LoginResponse of(String accessToken, String refreshToken) {
            return new LoginResponse("Bearer", accessToken, refreshToken);
        }
    }

    public record LoginInfoResponse(
            String email,
            String pw,
            Long memberNo,
            Long loginNo
    ) {}
}