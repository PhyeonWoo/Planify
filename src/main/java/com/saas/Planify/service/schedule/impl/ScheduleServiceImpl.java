package com.saas.Planify.service.schedule.impl;

import com.saas.Planify.mapper.schedule.ScheduleMapper;
import com.saas.Planify.mapper.workspace.WorkSpaceMapper;
import com.saas.Planify.model.dto.schedule.ScheduleDto;
import com.saas.Planify.model.dto.workspace.WorkSpaceDto;
import com.saas.Planify.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final WorkSpaceMapper workSpaceMapper;

    @Override
    public void createSchedule(Long memberNo, Long workSpaceNo, ScheduleDto.ScheduleRequest request) {
        WorkSpaceDto.WorkSpaceFlatDto response = workSpaceMapper.singleWorkSpace(workSpaceNo);
        if(response == null) {
            throw new IllegalArgumentException("생성이 불가합니다.");
        }
        scheduleMapper.createSchedule(memberNo,workSpaceNo,request);
    }

    @Override
    public void updateSchedule(Long memberNo, Long scheduleNo, ScheduleDto.ScheduleUpdate request) {
        ScheduleDto.ScheduleResponse response = scheduleMapper.singleResponse(scheduleNo);
        if(response.creatorNo().equals(memberNo)) {
            throw new IllegalArgumentException("일치하지 않아서 수정불가능");
        }
        scheduleMapper.updateSchedule(scheduleNo, memberNo, request);
    }

    @Override
    public void deleteSchedule(Long scheduleNo, Long memberNo) {
        ScheduleDto.ScheduleResponse response = scheduleMapper.singleResponse(scheduleNo);
        if(response.creatorNo().equals(memberNo)) {
            throw new IllegalArgumentException("일치하지 않아서 수정불가능");
        }
        scheduleMapper.deleteSchedule(scheduleNo, memberNo);
    }
}
