package com.saas.Planify.mapper.auth;

import com.saas.Planify.model.dto.auth.AuthDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {

    // Member DB에 생성
    void insertMember(AuthDto.LoginCreateRequest request);

    // 마지막 ID 조회
    Long lastInsertId();


    // ID 즉 Email 동일 여부 조회
    boolean existByEmail(String email);


    AuthDto.LoginInfoResponse findByEmail(String email);
}
