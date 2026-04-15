package com.saas.Planify.mapper.workspace;


import com.saas.Planify.model.dto.workspace.WorkSpaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkSpaceMapper {

    Long lastInsertId();

    void insertWorkSpace(@Param("memberNo") Long memberNo,
                         @Param("request") WorkSpaceDto.WorkSpaceCreateRequest request);

    void updateWorkSpace(
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("ownerNo") Long ownerNo,
            @Param("request") WorkSpaceDto.WorkSpaceUpdateRequest request);

    void deleteWorkSpace(
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("ownerNo") Long ownerNo);

    // Workspace 단건 조회
    WorkSpaceDto.WorkSpaceFlatDto singleWorkSpace(Long workSpaceNo);

    // 특정 회원이 속한 workspace 전체 조회
    List<WorkSpaceDto.WorkSpaceFlatDto> MemberWorkSpace(Long memberNo);



    // 워크스페이스에 멤버 저장
    void insertWorkSpaceMember(WorkSpaceDto.WorkSpaceMemberCreateRequest request);

    // 워크스페이스 멤버 역할 수정
    void updateMemberRole(
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("targetMemberNo") Long targetMemberNo,
            @Param("role") String role);

    // 워크스페이스에서 멤버 삭제
    void deleteWorkSpaceMember(
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("memberNo") Long memberNo);



    // 워크스페이스 내 멤버 전체 조회
    List<WorkSpaceDto.WorkSpaceFlatMember> workSpaceAllMember(Long workSpaceNo);

    // 워크스페이스 내 특정 멤버 조회
    WorkSpaceDto.WorkSpaceFlatMember singleWorkSpaceMember(
            @Param("memberNo") Long memberNo,
            @Param("workSpaceNo") Long workSpaceNo);



    //초대 링크 생성
    void createInvite(Map<String, Object> param);

    //초대 토큰으로 초대 정보 조회
    WorkSpaceDto.WorkSpaceInviteFlat selectInviteToken(String inviteToken);

    // 초대 사용 횟수 +1 증가
    void incrementInviteUsedCount(String inviteToken);
}