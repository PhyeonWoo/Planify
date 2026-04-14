package com.saas.Planify.controller.notification;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.notification.NotificationDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ApiResponse<String> createNotification(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody NotificationDto.NotificationCreateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        notificationService.createNotification(memberNo, request);
        return ApiResponse.ok("생성 완료");
    }


    @DeleteMapping("/delete/{notiNo}")
    public ApiResponse<String> deleteNotification(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long notiNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        notificationService.deleteNotification(memberNo, notiNo);
        return ApiResponse.ok("삭제 완료");
    }


    @GetMapping("/all")
    public ApiResponse<List<NotificationDto.NotificationListResponse>> memberAllNoti(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<NotificationDto.NotificationListResponse> response = notificationService.memberAllNoti(memberNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/single/{notiNo}")
    public ApiResponse<NotificationDto.NotificationResponse> singleNoti(
            @PathVariable Long notiNo
    ) {
        NotificationDto.NotificationResponse response = notificationService.singleNoti(notiNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/unread")
    public ApiResponse<List<NotificationDto.NotificationListResponse>> unreadNoti(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<NotificationDto.NotificationListResponse> response = notificationService.unreadNoti(memberNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/allRead")
    public ApiResponse<String> allReadNoti(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        notificationService.allReadNoti(memberNo);
        return ApiResponse.ok("모두 읽음처리 완료");
    }


    @GetMapping("/setting")
    public ApiResponse<NotificationDto.NotificationSettingResponse> settingNoti(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        NotificationDto.NotificationSettingResponse response =notificationService.settingNoti(memberNo);
        return ApiResponse.ok(response);
    }


    @PutMapping("/setting/{settingNo}")
    public ApiResponse<String> updateSetting(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long settingNo,
            @RequestBody NotificationDto.NotificationSettingRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        notificationService.updateSetting(memberNo, settingNo, request);
        return ApiResponse.ok("수정 완료");
    }



}
