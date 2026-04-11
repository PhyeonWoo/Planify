package com.saas.Planify.model.dto.workspace;

import java.time.LocalDate;

public class WorkSpaceDto {


    // ============== Request ===============
    // workspace 요청
    public record WorkSpaceCreateRequest(
//            Long ownerNo, //SAAS_MEMBER DB
            String workSpaceName, // workspace name
            String description,  // workspace description
            String type // personal, team
    ) {}


    // workspace 수정
    public record WorkSpaceUpdateRequest(
//            Long workSpaceNo,
//            Long ownerNo,
            String workSpaceName, // workspace name
            String description,  // workspace description
            String type // personal, team
    ) {}


    public record UpdateMemberRoleRequest(
            String role  // "MEMBER", "ADMIN" 등
    ) {}

    // 초대 링크 생성 요청
    public record CreateInviteRequest(
            String role,
            int expireHours
    ) {}




    // ============== Response ===============

    // Controller 응답용 DTO
    public static class WorkSpaceControllerResponse {
        public Long workSpaceNo;
        public String workSpaceName;
        public String description;
        public String type;
        public String ownerNickname; // member DB내에 nickname
        public LocalDate createdDt;
    }



    // Controller용 DTO
    public static class WorkSpaceInviteResponse {
        public Long inviteNo;
        public String inviteToken;
        public String inviteLink;
        public String role;
        public String expiredDt;
        public int usedCount;
    }


    // ============== Flat Response ===============

    // 내부용 DTO - workSpace 정보 조회
    public record WorkSpaceFlatDto(
            Long workSpaceNo,
            Long ownerNo, // memberNo
            String ownerNickname, // SAAS_MEMBER 내 nickname
            String workSpaceName,
            String description,
            String type,
            LocalDate createdDt,
            LocalDate updatedDt
    ) {}



    // 내부용 DTO - 멤버 목록 조회 (workspace 내 멤버 조회)
    public record WorkSpaceFlatMember(
            Long wsMemberNo,
            Long workSpaceNo,
            Long memberNo,
            String nickname,
            String email,
            String role,
            LocalDate joinedDt
    ) {}


    // WorkSpace 초대 내부용 DTO
    public record WorkSpaceInviteFlat (
            Long inviteNo,
            Long workSpaceNo,
            String inviteToken,
            String role,
            String expiredDt,
            int useCount,
            String inviterNickName // SAAS_MEMBER DB 내 nickname
    ) {}




    public record WorkSpaceMemberCreateRequest(
            Long workSpaceNo,
            Long memberNo,
            String role
    ) {
        public static WorkSpaceMemberCreateRequest of(Long workSpaceNo, Long memberNo, String role) {
            return new WorkSpaceMemberCreateRequest(workSpaceNo,memberNo, role);
        }
    }



}
