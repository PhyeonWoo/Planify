//package com.saas.Planify.service.notification.assembler;
//
//import com.saas.Planify.model.dto.notification.NotificationDto;
//
//public class NotificationAssembler {
//
//    // NotificationFlatDto -> NotificationResponse 변환
//    public static NotificationDto.NotificationResponse toResponse(NotificationDto.NotificationFlatDto flat) {
//        if (flat == null) return  null;
//        NotificationDto.NotificationResponse response =
//                new NotificationDto.NotificationResponse();
//        response.notiNo = flat.notiNo();
//        response.memberNo = flat.memberNo();
//        response.type = flat.type();
//        response.content = flat.content();
//        response.title = flat.title();
//        response.refType = flat.refType();
//        response.refId = flat.refId();
//        response.isRead = flat.isRead();
//        response.createdDt = flat.createdDt();
//        response.readDt = flat.readDt();
//
//        return response;
//    }
//
//    // NotificationFlatDto -> NotificationListResponse 반환
//    public static NotificationDto.NotificationListResponse toListResponse(NotificationDto.NotiListFlatDto flat) {
//        if(flat == null) return null;
//        NotificationDto.NotificationListResponse response =
//                new NotificationDto.NotificationListResponse();
//        response.notiNo = flat.notiNo();
//        response.type = flat.type();
//        response.title = flat.title();
//        response.content = flat.content();
//        response.isRead = flat.isRead();
//        response.createdDt = flat.createdDt();
//
//        return response;
//    }
//
////
////    // List<NotificationFlatDto> ->  List<NotificaitonListResponse> 변환
////    public static List<NotificationDto.NotificationListResponse> toList(List<NotificationDto.NotificationFlatDto> list) {
////        if (list == null || list.isEmpty()) {
////            return List.of();
////        }
////
////        return list.stream()
////                .map(NotificationAssembler::toListResponse)
////                .collect(Collectors.toList());
////    }
////
//
//    // NotificaionSettingFlatDto -> NotificationSettingResponse 반환
//    private static NotificationDto.NotificationSettingResponse toSettingResponse(NotificationDto.NotificationSettingFlatDto flat) {
//        if(flat == null) return  null;
//
//        NotificationDto.NotificationSettingResponse response = new NotificationDto.NotificationSettingResponse();
//        response.settingNo = flat.settingNo();
//        response.memberNo = flat.memberNo();
//        response.taskDuePush = flat.taskDuePush();
//        response.taskDueEmail = flat.taskDueEmail();
//        response.commentPush = flat.commentPush();
//        response.commentEmail = flat.commentEmail();
//        response.invitePush = flat.invitePush();
//        response.inviteEmail = flat.inviteEmail();
//        response.updatedD = flat.updatedDt();
//
//        return response;
//    }
//
//    private NotificationAssembler() {}
//}
