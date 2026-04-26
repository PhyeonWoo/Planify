package com.saas.Planify.mapper.schedule;

import com.saas.Planify.model.dto.schedule.ScheduleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleMapper {

    int createSchedule(
            @Param("creatorNo") Long creatorNo,
            @Param("workSpaceNo") Long workSpaceNo,
            @Param("request") ScheduleDto.ScheduleCreateRequest request
    );

    int updateSchedule(
            @Param("scheduleNo") Long scheduleNo,
            @Param("request") ScheduleDto.ScheduleUpdateRequest request
    );

    int deleteSchedule(Long scheduleNo);

    ScheduleDto.ScheduleFlatDto singleSchedule(Long scheduleNo);

    List<ScheduleDto.ScheduleFlatDto> mySchedules(Long memberNo);

    List<ScheduleDto.ScheduleFlatDto> workSpaceSchedules(Long workSpaceNo);
}
