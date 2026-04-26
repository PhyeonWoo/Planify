package com.saas.Planify.model.dto.schedule;

import java.time.LocalDateTime;

public class ScheduleDto {

    // 스케줄 생성 요청
    public record ScheduleCreateRequest(
            String title,
            String description,
            String location,
            LocalDateTime startDt,
            LocalDateTime endDt,
            boolean isAllDay
    ) {}

    public record ScheduleUpdateRequest(
            String title,
            String description,
            String location,
            LocalDateTime startDt,
            LocalDateTime endDt,
            boolean isAllDay
    ) {}

    public record ScheduleFlatDto(
            Long scheduleNo,
            Long workspaceNo,
            Long creatorNo,
            String creatorNickname,
            String title,
            String description,
            String location,
            LocalDateTime startDt,
            LocalDateTime endDt,
            boolean isAllDay,
            LocalDateTime createdDt,
            LocalDateTime updatedDt
    ) {}

    public static class ScheduleResponse {
        public Long scheduleNo;
        public Long workspaceNo;
        public Long creatorNo;
        public String creatorNickname;
        public String title;
        public String description;
        public String location;
        public LocalDateTime startDt;
        public LocalDateTime endDt;
        public boolean isAllDay;
        public LocalDateTime createdDt;
        public LocalDateTime updatedDt;
    }
}
