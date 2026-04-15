package com.saas.Planify.model.dto.notification;

import org.threeten.bp.LocalDateTime;

public class NotificationDto {
    public record NotificationCreateRequest(
            String type,
            String title,
            String content,
            String refType // Task, schedule
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
        public Boolean isRead;
        public LocalDateTime readDt;
        public LocalDateTime createdDt;
    }

    public static class NotificationListResponse{
        public Long notiNo;
        public Long memberNo;
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
        public LocalDateTime updatedDt;
    }

}
