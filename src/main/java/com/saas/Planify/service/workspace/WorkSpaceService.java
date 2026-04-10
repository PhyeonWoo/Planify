package com.saas.Planify.service.workspace;

import com.saas.Planify.model.dto.workspace.WorkSpaceDto;

import java.util.List;

public interface WorkSpaceService {

    void insertWorkSpace(Long memberNo, WorkSpaceDto.WorkSpaceCreateRequest request);

    void updateWorkSpace(Long workSpaceNo, Long memberNo, WorkSpaceDto.WorkSpaceUpdateRequest request);

    void deleteWorkSpace(Long workSpaceNo, Long memberNo);

    WorkSpaceDto.WorkSpaceControllerResponse singleWorkSpace(Long workSpaceNo);

    List<WorkSpaceDto.WorkSpaceControllerResponse> MemberWorkSpace(Long memberNo);



    // 워크스페이스 내 멤버 전체 조회
    List<WorkSpaceDto.WorkSpaceFlatMember> workSpaceAllMember(Long workSpaceNo);

    void updateMemberRole(Long workSpaceNo, Long memberNo, Long targetMemberNo, String role);
    void deleteWorkSpaceMember(Long workSpaceNo, Long memberNo, Long targetMemberNo);




    WorkSpaceDto.WorkSpaceInviteResponse createInvite(Long workSpaceNo, Long memberNo, WorkSpaceDto.CreateInviteRequest request);

    void joinByInvite(String inviteToken, Long memberNo);

}
