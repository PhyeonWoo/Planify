package com.saas.Planify.mapper.auth;

import com.saas.Planify.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    // Member DB 생성
    void insertMember(AuthDto.MemberCreateRequest request);

    // Login DB 생성
    void insertLogin(AuthDto.LoginCreateRequest request);

    void updateNickName(@Param("memberNo") Long memberNo,
                        @Param("req") AuthDto.MemberNicknameUpdate req);


    Long lastInsertId();

    // ID 즉 Email 동일 여부 조회
    boolean existByEmail(String email);

    boolean existByNickname(String nickname);

    AuthDto.LoginInfoResponse findByEmail(String email);

    AuthDto.LoginInfoResponse findByMemberNo(Long memberNo);

    AuthDto.MemberProfileResponse getProfile(Long memberNo);
}