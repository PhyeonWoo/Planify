package com.saas.Planify.service.schedule;

import com.saas.Planify.model.dto.schedule.ScheduleDto;

public interface ScheduleService {
    void createSchedule(
            Long memberNo,
            Long workSpaceNo,
            ScheduleDto.ScheduleRequest request
    );

    void updateSchedule(
            Long memberNo,
            Long scheduleNo,
            ScheduleDto.ScheduleUpdate request
    );

    void deleteSchedule(
            Long scheduleNo,
            Long memberNo
    );

}
