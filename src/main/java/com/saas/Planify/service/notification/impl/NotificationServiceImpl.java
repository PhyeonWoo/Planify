package com.saas.Planify.service.notification.impl;

import com.saas.Planify.mapper.notification.NotificationMapper;
import com.saas.Planify.model.dto.notification.NotificationDto;
import com.saas.Planify.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;

    @Override
    public void createNotification(Long memberNo, NotificationDto.NotificationCreateRequest request) {
        int count = notificationMapper.createNotification(memberNo, request);
        if(count == 0) {
            throw new IllegalArgumentException("저장 안됨");
        }
    }

    @Override
    public void deleteNotification(Long memberNo, Long notiNo) {
       NotificationDto.NotificationResponse response = notificationMapper.singleNoti(notiNo);
       if(response == null) {
           throw new IllegalArgumentException("존재하지 않음");
       }
       if(!memberNo.equals(response.memberNo)) {
           throw new IllegalArgumentException("일치하지 않음");
       }
       notificationMapper.deleteNotification(memberNo, notiNo);
    }

    @Override
    public NotificationDto.NotificationResponse singleNoti(Long notiNo) {
        NotificationDto.NotificationResponse response = notificationMapper.singleNoti(notiNo);

        if(response == null) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        return response;
    }

    @Override
    public List<NotificationDto.NotificationListResponse> memberAllNoti(Long memberNo) {
        List<NotificationDto.NotificationListResponse> response = notificationMapper.memberAllNoti(memberNo);
        if (response.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        if (!response.equals(memberNo)) {
            throw new IllegalArgumentException("일치하지 않습니다");
        }
        return response;
    }

    @Override
    public List<NotificationDto.NotificationListResponse> unreadNoti(Long memberNo) {
        List<NotificationDto.NotificationListResponse> response = notificationMapper.unreadNoti(memberNo);
        if (response.isEmpty()) {
            throw new IllegalArgumentException("없음");
        }
        return response;
    }

    // 전부 읽음처리하기
    @Override
    public void allReadNoti(Long memberNo) {
        List<NotificationDto.NotificationListResponse> response = notificationMapper.unreadNoti(memberNo);
        if(response.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        notificationMapper.allReadNoti(memberNo);
    }


    // 세팅값 불러오기
    @Override
    public NotificationDto.NotificationSettingResponse settingNoti(Long memberNo) {
        NotificationDto.NotificationSettingResponse response = notificationMapper.settingNoti(memberNo);
        if (response == null) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        return response;
    }

    @Override
    public void updateSetting(Long settingNo, Long memberNo, NotificationDto.NotificationSettingRequest request) {
       int count = notificationMapper.updateSetting(settingNo, memberNo, request);
       if(count == 0) {
           throw new IllegalArgumentException("저장 안됨");
       }
    }
}
