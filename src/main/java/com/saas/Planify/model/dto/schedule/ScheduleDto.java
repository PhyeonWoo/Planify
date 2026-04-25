package com.saas.Planify.model.dto.schedule;

import com.google.api.client.util.DateTime;

public class ScheduleDto {
    public record ScheduleRequest(
            Long scheduleNo,
            Long workSpaceNo,
            Long creatorNo,
            String title,
            String description,
            String location,
            DateTime startDt,
            DateTime endDt,
            boolean isAllDay,
            DateTime createdDt,
            DateTime updatedDt
    ) {}


    public record ScheduleUpdate(
            String title,
            String description,
            String location,
            DateTime startDt,
            DateTime endDt,
            boolean isAllDay
    ) {}

    public record ScheduleResponse(
            Long creatorNo,
            Long workSpaceNo,
            String title,
            String description,
            String location,
            DateTime startDt,
            DateTime endDt,
            boolean isAllDay
    ) {}
}
