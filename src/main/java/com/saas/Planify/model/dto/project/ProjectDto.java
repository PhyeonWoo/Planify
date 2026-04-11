package com.saas.Planify.model.dto.project;

import com.google.api.client.util.DateTime;
import com.saas.Planify.model.dto.workspace.WorkSpaceDto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ProjectDto {

    // ============== Request ===============

    //project 생성 요청
    public record ProjectCreateRequest(
            String name,
            String description,
            Date startDt,
            Date endDt,
            List<Long> MemberNos
    ) {}

    // Project 수정 요청
    public record ProjectUpdateRequest(
            String name,
            String description,
            Date endDt,
            List<Long> memberNos
    ) {}


    // Project내 member의 역할 수정 요청
    public record UpdateProjectMemberRoleRequest(
            String role
    ) {}


    // ============== Response ===============

    // 참여 사용자 정보 출력
    public record MemberInfoResponse(
            Long memberNo,
            String nickname
    ) {}

    // Project 상태 응답
    public static class ProjectResponse{
            public Long projectNo;
            public String name;
            public String description;
            public Date startDt;
            public Date endDt;
            public List<MemberInfoResponse> members;
}

    // ============== Flat Response ===============

    public record ProjectFlatResponse(
            Long projectNo,
            Long memberNo,
            String nickname,
            String name,
            String description,
            Date startDt,
            Date endDt,
            LocalDateTime createdDt,
            LocalDateTime updatedDt
    ) {}


    public record ProjectMemberFlatResponse(
            Long pjMemberNo,
            Long projectNo,
            Long memberNo,
            String nickname,
            String email,
            String role
    ) {}




    public record ProjectMemberCreateRequest(
            Long projectNo,
            Long memberNo,
            String role
    ) {
        public static WorkSpaceDto.WorkSpaceMemberCreateRequest of(Long projectNo, Long memberNo, String role) {
            return new WorkSpaceDto.WorkSpaceMemberCreateRequest(projectNo,memberNo, role);
        }
    }




}
