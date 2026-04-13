package com.saas.Planify.mapper.notification;

import com.saas.Planify.model.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {
    Long lastInsertId();

    int createNotification(
            @Param("memberNo") Long memberNo,
            @Param("request")NotificationDto.NotificationCreateRequest request
    );

    // 알림 단건 조회
    NotificationDto.NotificationFlatDto singleNoti(
            Long notiNo
    );

    // 회원 알림 전체 조회
    List<NotificationDto.NotificationFlatDto> memberAllNoti(
            Long memberNo
    );

    // mapper 에서 isRead False 인거 조회해오기
    List<NotificationDto.NotificationFlatDto> unreadNoti(
            Long memberNo
    );

    // 모두 읽음처리 하기
    void allReadNoti(Long memberNo);

    // 단건 읽음처리 하기
    void singleReadNoti(Long notiNo);

    void deleteNotification(
            @Param("memberNo") Long memberNo,
            @Param("notiNo") Long notiNo
    );



    // notification Setting 부분

    // 현재 알림 설정 정보 조회
    NotificationDto.NotificationSettingFlatDto settingNoti(Long memberNo);


    // 알림 설정 수정
    void updateSetting(
           @Param("memberNo") Long memberNo,
           @Param("request") NotificationDto.NotificationSettingRequest request
    );
}
