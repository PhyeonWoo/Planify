package com.saas.Planify.service.notification;

import com.saas.Planify.model.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {
    void createNotification(
            Long memberNo,
            NotificationDto.NotificationCreateRequest request
    );

    void deleteNotification(
            Long memberNo,
            Long notiNo
    );

    NotificationDto.NotificationResponse singleNoti(
            Long notiNo
    );

    List<NotificationDto.NotificationListResponse> memberAllNoti(
            Long memberNo
    );

    List<NotificationDto.NotificationListResponse> unreadNoti(
            Long memberNo
    );

    void allReadNoti(
            Long memberNo
    );

    void singleReadNoti(
            Long notiNo
    );

    NotificationDto.NotificationSettingResponse settingNoti(
            Long memberNo
    );

    void updateSetting(
            Long memberNo,
            NotificationDto.NotificationSettingRequest request
    );

}
