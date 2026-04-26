package com.saas.Planify.service.schedule;

import com.saas.Planify.model.dto.schedule.ScheduleDto;

import java.util.List;

public interface ScheduleService {

    void createSchedule(Long memberNo, Long workSpaceNo, ScheduleDto.ScheduleCreateRequest request);

    void updateSchedule(Long memberNo, Long scheduleNo, ScheduleDto.ScheduleUpdateRequest request);

    void deleteSchedule(Long scheduleNo, Long memberNo);

    ScheduleDto.ScheduleResponse getSchedule(Long scheduleNo);

    List<ScheduleDto.ScheduleResponse> getMySchedules(Long memberNo);

    List<ScheduleDto.ScheduleResponse> getWorkSpaceSchedules(Long workSpaceNo);
}
