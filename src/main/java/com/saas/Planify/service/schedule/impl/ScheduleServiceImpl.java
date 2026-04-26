package com.saas.Planify.service.schedule.impl;

import com.saas.Planify.mapper.schedule.ScheduleMapper;
import com.saas.Planify.mapper.workspace.WorkSpaceMapper;
import com.saas.Planify.model.dto.schedule.ScheduleDto;
import com.saas.Planify.model.dto.workspace.WorkSpaceDto;
import com.saas.Planify.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final WorkSpaceMapper workSpaceMapper;

    @Override
    @Transactional
    public void createSchedule(Long memberNo, Long workSpaceNo, ScheduleDto.ScheduleCreateRequest request) {
        WorkSpaceDto.WorkSpaceFlatDto ws = workSpaceMapper.singleWorkSpace(workSpaceNo);
        if (ws == null) {
            throw new IllegalArgumentException("워크스페이스가 없습니다.");
        }
        int count = scheduleMapper.createSchedule(memberNo, workSpaceNo, request);
        if (count == 0) {
            throw new IllegalArgumentException("일정 생성에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void updateSchedule(Long memberNo, Long scheduleNo, ScheduleDto.ScheduleUpdateRequest request) {
        ScheduleDto.ScheduleFlatDto schedule = scheduleMapper.singleSchedule(scheduleNo);
        if (schedule == null) {
            throw new IllegalArgumentException("일정이 없습니다.");
        }
        if (!schedule.creatorNo().equals(memberNo)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        scheduleMapper.updateSchedule(scheduleNo, request);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleNo, Long memberNo) {
        ScheduleDto.ScheduleFlatDto schedule = scheduleMapper.singleSchedule(scheduleNo);
        if (schedule == null) {
            throw new IllegalArgumentException("일정이 없습니다.");
        }
        if (!schedule.creatorNo().equals(memberNo)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        scheduleMapper.deleteSchedule(scheduleNo);
    }

    @Override
    public ScheduleDto.ScheduleResponse getSchedule(Long scheduleNo) {
        ScheduleDto.ScheduleFlatDto flat = scheduleMapper.singleSchedule(scheduleNo);
        if (flat == null) {
            throw new IllegalArgumentException("일정이 없습니다.");
        }
        return convertToResponse(flat);
    }

    @Override
    public List<ScheduleDto.ScheduleResponse> getMySchedules(Long memberNo) {
        return scheduleMapper.mySchedules(memberNo).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto.ScheduleResponse> getWorkSpaceSchedules(Long workSpaceNo) {
        return scheduleMapper.workSpaceSchedules(workSpaceNo).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ScheduleDto.ScheduleResponse convertToResponse(ScheduleDto.ScheduleFlatDto flat) {
        ScheduleDto.ScheduleResponse response = new ScheduleDto.ScheduleResponse();
        response.scheduleNo = flat.scheduleNo();
        response.workspaceNo = flat.workspaceNo();
        response.creatorNo = flat.creatorNo();
        response.creatorNickname = flat.creatorNickname();
        response.title = flat.title();
        response.description = flat.description();
        response.location = flat.location();
        response.startDt = flat.startDt();
        response.endDt = flat.endDt();
        response.isAllDay = flat.isAllDay();
        response.createdDt = flat.createdDt();
        response.updatedDt = flat.updatedDt();
        return response;
    }
}
