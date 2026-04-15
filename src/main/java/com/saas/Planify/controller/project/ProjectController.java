package com.saas.Planify.controller.project;


import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.project.ProjectDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final ProjectService projectService;
    private final JwtProvider jwtProvider;

    @PostMapping("/{workSpaceNo}")
    public ApiResponse<String> createProject(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long workSpaceNo,
            @RequestBody ProjectDto.ProjectCreateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        projectService.createProject(memberNo, workSpaceNo, request);
        return ApiResponse.ok("생성 완료");
    }


    @PutMapping("/{projectNo}")
    public ApiResponse<String> updateProject(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long projectNo,
            @RequestBody ProjectDto.ProjectUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        projectService.updateProject(projectNo, memberNo, request);
        return ApiResponse.ok("수정 완료");
    }


    @DeleteMapping("/{projectNo}")
    public ApiResponse<String> deleteProject(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long projectNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        projectService.deleteProject(projectNo, memberNo);
        return ApiResponse.ok("삭제 완료");
    }


    @GetMapping("/single/{projectNo}")
    public ApiResponse<ProjectDto.ProjectResponse> singleProject(
            @PathVariable Long projectNo
    ) {
        ProjectDto.ProjectResponse response = projectService.singleProject(projectNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/member")
    public ApiResponse<List<ProjectDto.ProjectResponse>> memberProject(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<ProjectDto.ProjectResponse> response = projectService.MemberProject(memberNo);
        return ApiResponse.ok(response);
    }

    @GetMapping("/allMember/{projectNo}")
    public ApiResponse<List<ProjectDto.ProjectMemberFlatResponse>> projectAllMember(
            @PathVariable Long projectNo
    ) {
        List<ProjectDto.ProjectMemberFlatResponse> response = projectService.projectAllMember(projectNo);
        return ApiResponse.ok(response);
    }


    @PutMapping("/{projectNo}/member/{targetMemberNo}/role")
    public ApiResponse<String> updateMemberRole(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long targetMemberNo,
            @PathVariable Long projectNo,
            @RequestBody ProjectDto.UpdateProjectMemberRoleRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        projectService.updateMemberRole(
                projectNo,
                memberNo,
                targetMemberNo,
                request.role()
        );
        return ApiResponse.ok("변경 완료");
    }



    @DeleteMapping("/{projectNo}/member/{targetMemberNo}")
    public ApiResponse<String> deleteProjectMember(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long projectNo,
            @PathVariable Long targetMemberNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        projectService.deleteMember(
                projectNo,
                memberNo,
                targetMemberNo
        );
        return ApiResponse.ok("삭제 완료");
    }
}
