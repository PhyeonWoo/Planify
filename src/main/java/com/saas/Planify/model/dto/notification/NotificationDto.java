package com.saas.Planify.model.dto.notification;

import org.threeten.bp.LocalDateTime;

public class NotificationDto {
    public record NotificationCreateRequest(
            String type,
            String title,
            String content,
            String refType, // Task, schedule 등
            Long refId
    ) {}

    public record NotificationReadRequest(
            Boolean isRead
    ) {}

    public record NotificationSettingRequest(
            Boolean taskDuePush,
            Boolean taskDueEmail,
            Boolean commentPush,
            Boolean commentEmail,
            Boolean invitePush,
            Boolean inviteEmail,
            LocalDateTime updatedDt
    ) {}


    // Noti 상세 응답
    public static class NotificationResponse {
        public Long notiNo;
        public Long memberNo;
        public String type;
        public String title;
        public String content;
        public String refType;
        public Long refId;
        public Boolean isRead;
        public LocalDateTime readDt;
        public LocalDateTime createdDt;
    }

    public static class NotificationListResponse{
        public Long notiNo;
        public String type;
        public String title;
        public String content;
        public Boolean isRead;
        public LocalDateTime createdDt;
    }

    public static class NotificationSettingResponse{
        public Long settingNo;
        public Long memberNo;
        public Boolean taskDuePush;
        public Boolean taskDueEmail;
        public Boolean commentPush;
        public Boolean commentEmail;
        public Boolean invitePush;
        public Boolean inviteEmail;
        public LocalDateTime updatedD;
    }


    // 내부용 DTO
    public record NotificationFlatDto(
            Long notiNo,
            Long memberNo,
            String type,
            String title,
            String content,
            String refType,
            Long refId,
            Boolean isRead,
            LocalDateTime readDt,
            LocalDateTime createdDt
    ) {}


    public record NotificationSettingFlatDto(
            Long settingNo,
            Long memberNo,
            Boolean taskDuePush,
            Boolean taskDueEmail,
            Boolean commentPush,
            Boolean commentEmail,
            Boolean invitePush,
            Boolean inviteEmail,
            LocalDateTime updatedDt
    ) {}


}
