package com.saas.Planify.service.project.assembler;

import com.saas.Planify.model.dto.project.ProjectDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ProjectAssembler {

    public static List<ProjectDto.ProjectResponse> toGroup(List<ProjectDto.ProjectFlatResponse> list) {
        if (list == null || list.isEmpty()) return List.of();

        return list.stream()

                .collect(Collectors.groupingBy(
                        ProjectDto.ProjectFlatResponse::projectNo,
                        LinkedHashMap::new,
                        toList()
                ))

                .entrySet().stream()
                .map(e -> {
                    ProjectDto.ProjectFlatResponse first = e.getValue().getFirst();

                    ProjectDto.ProjectResponse response = new ProjectDto.ProjectResponse();
                    response.projectNo = first.projectNo();
                    response.name = first.name();
                    response.description = first.description();
                    response.startDt = first.startDt();
                    response.endDt = first.endDt();

                    // 해당 프로젝트에 속한 멤버들을 리스트로 변환
                    response.members = e.getValue().stream()
                            .map(flat -> new ProjectDto.MemberInfoResponse(
                                    flat.memberNo(),
                                    flat.nickname()
                            ))
                            .collect(Collectors.toList());

                    return response;
                })
                .toList();
    }


    public static ProjectDto.ProjectResponse toSingle(ProjectDto.ProjectFlatResponse flat) {
        if (flat == null) return null;

        ProjectDto.ProjectResponse response = new ProjectDto.ProjectResponse();
        response.projectNo = flat.projectNo();
        response.name = flat.name();
        response.description = flat.description();
        response.startDt = flat.startDt();
        response.endDt = flat.endDt();

        // 멤버가 한 명뿐인 경우
        response.members = List.of(new ProjectDto.MemberInfoResponse(
                flat.memberNo(),
                flat.nickname()
        ));

        return response;
    }

    private ProjectAssembler() {}
}