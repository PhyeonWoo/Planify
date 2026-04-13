package com.saas.Planify.service.notification.impl;

import com.saas.Planify.mapper.notification.NotificationMapper;
import com.saas.Planify.model.dto.notification.NotificationDto;
import com.saas.Planify.service.notification.NotificationService;
import com.saas.Planify.service.notification.assembler.NotificationAssembler;
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
       NotificationDto.NotificationFlatDto response = notificationMapper.singleNoti(notiNo);
       if(response == null) {
           throw new IllegalArgumentException("존재하지 않음");
       }
       if(memberNo.equals(response.memberNo())) {
           throw new IllegalArgumentException("일치하지 않음");
       }
       notificationMapper.deleteNotification(memberNo, notiNo);
    }

    @Override
    public NotificationDto.NotificationResponse singleNoti(Long notiNo) {
        NotificationDto.NotificationFlatDto response = notificationMapper.singleNoti(notiNo);

        if(response == null) {
            throw new IllegalArgumentException("존재하지 않음");
        }
        return NotificationAssembler.toResponse(response);
    }

    @Override
    public List<NotificationDto.NotificationListResponse> memberAllNoti(Long memberNo) {
        return List.of();
    }

    @Override
    public List<NotificationDto.NotificationListResponse> unreadNoti(Long memberNo) {
        return List.of();
    }

    @Override
    public void allReadNoti(Long memberNo) {

    }

    @Override
    public void singleReadNoti(Long notiNo) {

    }

    @Override
    public NotificationDto.NotificationSettingResponse settingNoti(Long memberNo) {
        return null;
    }

    @Override
    public void updateSetting(Long memberNo, NotificationDto.NotificationSettingRequest request) {

    }
}
