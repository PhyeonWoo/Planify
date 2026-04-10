package com.saas.Planify.mapper.auth;

import com.saas.Planify.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {

    // Member DB 생성
    void insertMember(AuthDto.MemberCreateRequest request);

    // Login DB 생성
    void insertLogin(AuthDto.LoginCreateRequest request);


    Long lastInsertId();

    // ID 즉 Email 동일 여부 조회
    boolean existByEmail(String email);

    AuthDto.LoginInfoResponse findByEmail(String email);
}