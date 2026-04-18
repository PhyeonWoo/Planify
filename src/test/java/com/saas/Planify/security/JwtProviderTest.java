package com.saas.Planify.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret",
                "hello1234hello1234hello1234hello1234hello1234hello1234hello12341515123124");
        jwtProvider.init();
    }

    @Test
    @DisplayName("액세스 토큰 생성 후 memberNo 정상 추출")
    void createAccessToken_and_getMemberNo() {
        // given
        String token = jwtProvider.createAccessToken("test@test.com", 1L, "ROLE_USER");

        // when
        Long memberNo = jwtProvider.getMemberNo(token);

        // then
        assertThat(memberNo).isEqualTo(1L);
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_valid() {
        // given
        String token = jwtProvider.createAccessToken("test@test.com", 1L, "ROLE_USER");

        // when & then
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("위조된 토큰 검증 실패")
    void validateToken_invalid() {
        // given
        String fakeToken = "this.is.fake";

        // when & then
        assertThat(jwtProvider.validateToken(fakeToken)).isFalse();
    }

    @Test
    @DisplayName("Bearer 토큰에서 순수 토큰 추출")
    void resolveToken() {
        // given
        String bearerToken = "Bearer mytoken123";

        // when
        String token = jwtProvider.resolveToken(bearerToken);

        // then
        assertThat(token).isEqualTo("mytoken123");
    }

    @Test
    @DisplayName("Bearer 없는 헤더 입력 시 null 반환")
    void resolveToken_no_bearer() {
        // given
        String invalidHeader = "justtoken";

        // when
        String token = jwtProvider.resolveToken(invalidHeader);

        // then
        assertThat(token).isNull();
    }
}
