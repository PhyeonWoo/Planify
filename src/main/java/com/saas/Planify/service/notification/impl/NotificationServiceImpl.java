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
       notificationMapper.deleteNotification(notiNo);
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
        return notificationMapper.memberAllNoti(memberNo);
    }

    @Override
    public List<NotificationDto.NotificationListResponse> unreadNoti(Long memberNo) {
        List<NotificationDto.NotificationListResponse> response = notificationMapper.unreadNoti(memberNo);
        return response;
    }

    // 전부 읽음처리하기
    @Override
    public void allReadNoti(Long memberNo) {
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
        NotificationDto.NotificationSettingResponse response = notificationMapper.settingNoti(memberNo);

        if(response == null) {
            throw new IllegalArgumentException("설정이 존재하지 않음");
        }

        if(!response.memberNo.equals(memberNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

       int count = notificationMapper.updateSetting(settingNo, memberNo, request);
       if(count == 0) {
           throw new IllegalArgumentException("저장 안됨");
       }
    }
}
