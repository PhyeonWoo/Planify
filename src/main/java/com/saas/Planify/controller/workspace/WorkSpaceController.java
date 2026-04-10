package com.saas.Planify.controller.workspace;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.workspace.WorkSpaceDto;
import com.saas.Planify.security.UserPrincipal;
import com.saas.Planify.service.workspace.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workspace")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;

    @PostMapping
    public ApiResponse<String> createWorkSpace(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody WorkSpaceDto.WorkSpaceCreateRequest request
    ) {
        workSpaceService.insertWorkSpace(userPrincipal.getMemberNo(), request);
        return ApiResponse.ok("생성 완료");
    }



    @PutMapping("/{workSpaceNo}")
    public ApiResponse<String> updateWorkSpace(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long workSpaceNo,
            @RequestBody WorkSpaceDto.WorkSpaceUpdateRequest request
    ) {

        workSpaceService.updateWorkSpace(workSpaceNo, userPrincipal.getMemberNo(), request);
        return ApiResponse.ok("수정 완료");
    }


    @DeleteMapping("/delete/{workSpaceNo}")
    public ApiResponse<String> deleteWorkSpace(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long workSpaceNo
    ) {
        workSpaceService.deleteWorkSpace(workSpaceNo, userPrincipal.getMemberNo());
        return ApiResponse.ok("삭제 완료");
    }


    @GetMapping("/{workSpaceNo}")
    public ApiResponse<WorkSpaceDto.WorkSpaceControllerResponse> singleWorkSpace(
            @PathVariable Long workSpaceNo
    ) {
        WorkSpaceDto.WorkSpaceControllerResponse response = workSpaceService.singleWorkSpace(workSpaceNo);
        return ApiResponse.ok(response);
    }



    @GetMapping("/all")
    public ApiResponse<List<WorkSpaceDto.WorkSpaceControllerResponse>> MemberAllSpace(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<WorkSpaceDto.WorkSpaceControllerResponse> response =
                workSpaceService.MemberWorkSpace(userPrincipal.getMemberNo());

        return ApiResponse.ok(response);
    }



    @GetMapping("/all/{workSpaceNo}")
    public ApiResponse<List<WorkSpaceDto.WorkSpaceFlatMember>> workSpaceAllMember(
            @PathVariable Long workSpaceNo
    ) {
        List<WorkSpaceDto.WorkSpaceFlatMember> response = workSpaceService.workSpaceAllMember(workSpaceNo);

        return ApiResponse.ok(response);
    }


    @PutMapping("/{workSpaceNo}/member/{targetMemberNo}/role")
    public ApiResponse<String> updateMemberRole(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long workSpaceNo,
            @PathVariable Long targetMemberNo,
            @RequestBody WorkSpaceDto.UpdateMemberRoleRequest request
    ) {
        workSpaceService.updateMemberRole(
                workSpaceNo,
                userPrincipal.getMemberNo(),
                targetMemberNo,
                request.role()
        );
        return ApiResponse.ok("변경 완료");
    }


    @DeleteMapping("/{workSpaceNo}/member/{targetMemberNo}")
    public ApiResponse<String> deleteWorkSpaceMember(
            @PathVariable Long workSpaceNo,
            @PathVariable Long targetMemberNo,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        workSpaceService.deleteWorkSpaceMember(
                workSpaceNo,
                userPrincipal.getMemberNo(),
                targetMemberNo
        );
        return ApiResponse.ok("삭제 완료");
    }


    @PostMapping("/{workSpaceNo}/invite")
    public ApiResponse<WorkSpaceDto.WorkSpaceInviteResponse> createInvite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long workSpaceNo,
            @RequestBody WorkSpaceDto.CreateInviteRequest request
    ) {
        WorkSpaceDto.WorkSpaceInviteResponse response =
                workSpaceService.createInvite(
                        workSpaceNo,
                        userPrincipal.getMemberNo(),
                        request
                );
        return ApiResponse.ok(response);
    }



    @PostMapping("/join/{inviteToken}")
    public ApiResponse<String> joinByInvite(
            @PathVariable String inviteToken,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        workSpaceService.joinByInvite(inviteToken, userPrincipal.getMemberNo());
        return ApiResponse.ok("초대 완료");
    }
}
