package com.saas.Planify.service.project;

import com.saas.Planify.model.dto.project.ProjectDto;

import java.util.List;

public interface ProjectService {

    void createProject(
            Long memberNo,
            Long workSpaceNo,
            ProjectDto.ProjectCreateRequest request
    );

    void updateProject(
            Long projectNo,
            Long memberNo,
            ProjectDto.ProjectUpdateRequest request
    );

    void deleteProject(
            Long projectNo,
            Long memberNo
    );


    ProjectDto.ProjectResponse singleProject(
            Long projectNo
    );

    List<ProjectDto.ProjectResponse> MemberProject(
            Long memberNo
    );

    List<ProjectDto.ProjectMemberFlatResponse> projectAllMember(
            Long projectNo
    );


    void updateMemberRole(
            Long projectNo,
            Long memberNo,
            Long targetMemberNo,
            String role
    );


    void deleteMember(
            Long projectNo,
            Long memberNo,
            Long targetMemberNo
    );
}
