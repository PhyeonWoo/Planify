package com.saas.Planify.mapper.project;

import com.saas.Planify.model.dto.project.ProjectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    Long lastInsertId();

    void createProject(
            @Param("memberNo") Long memberNo,
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("request") ProjectDto.ProjectCreateRequest request
    );

    void updateProject(
            @Param("projectNo") Long projectNo,
            @Param("memberNo") Long memberNo,
            @Param("request") ProjectDto.ProjectUpdateRequest request
    );

    void deleteProject(
            @Param("projectNo") Long projectNo,
            @Param("memberNo") Long memberNo
    );


    ProjectDto.ProjectFlatResponse singleProject(Long projectNo);

    List<ProjectDto.ProjectFlatResponse> MemberProject(Long memberNo);

    void updateMemberRole(
            @Param("projectNo") Long projectNo,
            @Param("targetMemberNo") Long targetMemberNo,
            @Param("role") String role
    );

    void deleteMember(
            @Param("projectNo") Long projectNo,
            @Param("memberNo") Long memberNo
    );


    List<ProjectDto.ProjectMemberFlatResponse> projectAllMember(Long projectNo);

    ProjectDto.ProjectMemberFlatResponse singleProjectMember(
            @Param("projectNo") Long projectNo,
            @Param("memberNo") Long memberNo
    );



    void insertProjectMember(ProjectDto.ProjectMemberCreateRequest request);
}
