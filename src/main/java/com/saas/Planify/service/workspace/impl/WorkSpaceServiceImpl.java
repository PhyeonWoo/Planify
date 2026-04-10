package com.saas.Planify.service.workspace.impl;

import com.saas.Planify.mapper.workspace.WorkSpaceMapper;
import com.saas.Planify.model.dto.workspace.WorkSpaceDto;
import com.saas.Planify.service.workspace.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkSpaceServiceImpl implements WorkSpaceService {
    private final WorkSpaceMapper workSpaceMapper;

    @Value("${app.invite-base-url}")
    private String inviteBaseUrl;


    @Override
    public void insertWorkSpace(Long memberNo, WorkSpaceDto.WorkSpaceCreateRequest request) {
        workSpaceMapper.insertWorkSpace(request);
        Long workSpaceNo = workSpaceMapper.lastInsertId();

        WorkSpaceDto.WorkSpaceMemberCreateRequest memberRequest
                = WorkSpaceDto.WorkSpaceMemberCreateRequest.of(
                        workSpaceNo,
                request.memberNo(),
                "OWNER"
        );

        workSpaceMapper.insertWorkSpaceMember(memberRequest);
    }




    @Override
    public void updateWorkSpace(Long workSpaceNo, Long memberNo, WorkSpaceDto.WorkSpaceUpdateRequest request) {
        WorkSpaceDto.WorkSpaceFlatMember member = workSpaceMapper.singleWorkSpaceMember(workSpaceNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        workSpaceMapper.updateWorkSpace(request);
    }


    @Override
    public void deleteWorkSpace(Long workSpaceNo, Long memberNo) {
        WorkSpaceDto.WorkSpaceFlatMember member =
                workSpaceMapper.singleWorkSpaceMember(memberNo, workSpaceNo);

        if(member == null || (!"OWNER".equals(member.role()))) {
            throw new IllegalArgumentException("소유자만 가능합니다.");
        }

        workSpaceMapper.deleteWorkSpace(workSpaceNo, memberNo);
    }



    @Override
    public WorkSpaceDto.WorkSpaceControllerResponse singleWorkSpace(Long workSpaceNo) {
        WorkSpaceDto.WorkSpaceFlatDto flat = workSpaceMapper.singleWorkSpace(workSpaceNo);

        if(flat == null) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        return toResponse(flat);
    }



    @Override
    public List<WorkSpaceDto.WorkSpaceControllerResponse> MemberWorkSpace(Long memberNo) {
        return workSpaceMapper.MemberWorkSpace(memberNo).stream()
               .map(this::toResponse)
               .toList();
    }

    @Override
    public List<WorkSpaceDto.WorkSpaceFlatMember> workSpaceAllMember(Long workSpaceNo) {
        List<WorkSpaceDto.WorkSpaceFlatMember> response =
                workSpaceMapper.workSpaceAllMember(workSpaceNo);
        return response;
    }


    @Override
    public void updateMemberRole(Long workSpaceNo, Long memberNo, Long targetMemberNo, String role) {
        WorkSpaceDto.WorkSpaceFlatMember member =
                workSpaceMapper.singleWorkSpaceMember(workSpaceNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }


        WorkSpaceDto.WorkSpaceFlatMember target =
                workSpaceMapper.singleWorkSpaceMember(targetMemberNo, workSpaceNo);

        if(target == null) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        if("OWNER".equals(target.role())) {
            throw new IllegalArgumentException("소유자의 역할은 변경이 불가능합니다.");
        }

        workSpaceMapper.updateMemberRole(workSpaceNo, targetMemberNo, role);
    }




    @Override
    public void deleteWorkSpaceMember(Long workSpaceNo, Long targetMemberNo, Long memberNo) {
        // 삭제 요청자 권한 체크
        WorkSpaceDto.WorkSpaceFlatMember member =
                workSpaceMapper.singleWorkSpaceMember(workSpaceNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        //제거 대상 존재 확인
        WorkSpaceDto.WorkSpaceFlatMember response =
                workSpaceMapper.singleWorkSpaceMember(targetMemberNo, workSpaceNo);
        if (response == null) {
            throw new IllegalArgumentException("해당 멤버가 존재하지 않습니다.");
        }
        if ("OWNER".equals(response.role())) {
            throw new IllegalArgumentException("소유자는 제거할 수 없습니다.");
        }

        workSpaceMapper.deleteWorkSpaceMember(workSpaceNo, targetMemberNo);
    }




    @Override
    public WorkSpaceDto.WorkSpaceInviteResponse createInvite(Long workSpaceNo, Long memberNo, WorkSpaceDto.CreateInviteRequest request) {
        WorkSpaceDto.WorkSpaceFlatMember member = workSpaceMapper.singleWorkSpaceMember(workSpaceNo, memberNo);

        if(member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }


        String inviteToken = UUID.randomUUID().toString();
        String role = (request.role() != null) ? request.role() : "MEMBER";
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(request.expireHours());


        Map<String, Object> param = new HashMap<>();
        param.put("workSpaceNo",workSpaceNo);
        param.put("inviterNo", memberNo);
        param.put("inviteToken", inviteToken);
        param.put("role",role);
        param.put("expiredDt",expiredDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        workSpaceMapper.createInvite(param);

        WorkSpaceDto.WorkSpaceInviteResponse response = new WorkSpaceDto.WorkSpaceInviteResponse();
        response.inviteNo = (Long) param.get("inviteNo");
        response.inviteToken = inviteToken;
        response.inviteLink = inviteBaseUrl + inviteToken;
        response.role = role;
        response.expiredDt = expiredDate.toString();
        response.useCount = 0;
        return response;
    }

    @Override
    public void joinByInvite(String inviteToken, Long memberNo) {
        WorkSpaceDto.WorkSpaceInviteFlat flat = workSpaceMapper.selectInviteToken(inviteToken);

        if (flat == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if(LocalDateTime.parse(flat.expiredDt().replace(" ","T")).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 초대 링크 입니다.");
        }

        WorkSpaceDto.WorkSpaceFlatMember exist = workSpaceMapper.singleWorkSpaceMember(memberNo, flat.workSpaceNo());
        if(exist != null) {
            throw new IllegalArgumentException("이미 참여중인 워크스페이스 입니다.");
        }

        WorkSpaceDto.WorkSpaceMemberCreateRequest request = WorkSpaceDto.WorkSpaceMemberCreateRequest.of(
                flat.workSpaceNo(),
                memberNo,
                flat.role()
        );

        workSpaceMapper.insertWorkSpaceMember(request);

        workSpaceMapper.incrementInviteUsedCount(inviteToken);
    }





    public WorkSpaceDto.WorkSpaceControllerResponse toResponse(WorkSpaceDto.WorkSpaceFlatDto request) {
        WorkSpaceDto.WorkSpaceControllerResponse response = new WorkSpaceDto.WorkSpaceControllerResponse();
        response.workSpaceNo = request.workSpaceNo();
        response.workSpaceName = request.workSpaceName();
        response.description = request.description();
        response.type = request.type();
        response.ownerNickname = request.ownerNickname();
        response.createdDt = LocalDate.now();

        return response;
    }


}
