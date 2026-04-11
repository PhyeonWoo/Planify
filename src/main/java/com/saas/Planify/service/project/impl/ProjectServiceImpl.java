package com.saas.Planify.service.project.impl;


import com.saas.Planify.mapper.project.ProjectMapper;
import com.saas.Planify.model.dto.project.ProjectDto;
import com.saas.Planify.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.saas.Planify.service.project.assembler.ProjectAssembler.toGroup;
import static com.saas.Planify.service.project.assembler.ProjectAssembler.toSingle;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;


    @Override
    public void createProject(Long memberNo, Long workSpaceNo, ProjectDto.ProjectCreateRequest request) {

        projectMapper.createProject(memberNo,workSpaceNo,request);
        Long projectNo = projectMapper.lastInsertId();

        // OWNER 멤버 추가
        ProjectDto.ProjectMemberCreateRequest req =
                new ProjectDto.ProjectMemberCreateRequest(
                        projectNo,
                        memberNo,
                        "OWNER"
                );
        projectMapper.insertProjectMember(req);

        // 멤버 추가
        if(request.MemberNos() != null && !request.MemberNos().isEmpty()) {
            for(Long targetMemberNo : request.MemberNos()) {
                if(!targetMemberNo.equals(memberNo)) {
                    projectMapper.insertProjectMember(
                            new ProjectDto.ProjectMemberCreateRequest(
                                    projectNo, targetMemberNo, "MEMBER"
                            )
                    );
                }
            }
        }
    }

    @Override
    public void updateProject(Long projectNo, Long memberNo, ProjectDto.ProjectUpdateRequest request) {
        ProjectDto.ProjectMemberFlatResponse member = projectMapper.singleProjectMember(projectNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        projectMapper.updateProject(projectNo, memberNo, request);
    }


    @Override
    public void deleteProject(Long projectNo, Long memberNo) {
        ProjectDto.ProjectMemberFlatResponse member = projectMapper.singleProjectMember(projectNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        projectMapper.deleteProject(projectNo,memberNo);
    }



    @Override
    public ProjectDto.ProjectResponse singleProject(Long projectNo) {
        ProjectDto.ProjectFlatResponse flat = projectMapper.singleProject(projectNo);
        if(flat == null) {
            throw new IllegalArgumentException("존재하지 않습니다.");
        }

        return toSingle(flat);
    }

    @Override
    public List<ProjectDto.ProjectResponse> MemberProject(Long memberNo) {
        List<ProjectDto.ProjectFlatResponse> flat = projectMapper.MemberProject(memberNo);
        if(flat.isEmpty()) {
            return List.of();
        }

        return toGroup(flat);
    }

    @Override
    public List<ProjectDto.ProjectMemberFlatResponse> projectAllMember(Long projectNo) {
        List<ProjectDto.ProjectMemberFlatResponse> member = projectMapper.projectAllMember(projectNo);

        if(member.isEmpty()) {
            return List.of();
        }

        return member;
    }

    @Override
    public void updateMemberRole(Long projectNo, Long memberNo, Long targetMemberNo, String role) {
        ProjectDto.ProjectMemberFlatResponse member = projectMapper.singleProjectMember(projectNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        ProjectDto.ProjectMemberFlatResponse info = projectMapper.singleProjectMember(projectNo, targetMemberNo);

        if(info == null) {
            throw new IllegalArgumentException("존재하지 않습니다.");
        }
        if ("OWNER".equals(info.role())) {
            throw new IllegalArgumentException("소유자의 역할은 변경이 불가합니다.");
        }

        projectMapper.updateMemberRole(projectNo, targetMemberNo, role);
    }

    @Override
    public void deleteMember(Long projectNo, Long memberNo, Long targetMemberNo) {
        ProjectDto.ProjectMemberFlatResponse member = projectMapper.singleProjectMember(projectNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        ProjectDto.ProjectMemberFlatResponse info = projectMapper.singleProjectMember(projectNo, targetMemberNo);

        if(info == null) {
            throw new IllegalArgumentException("존재하지 않습니다.");
        }
        if ("OWNER".equals(info.role())) {
            throw new IllegalArgumentException("소유자는 제거가 불가합니다.");
        }

        projectMapper.deleteMember(projectNo, targetMemberNo);
    }

}
