package com.saas.Planify.model.dto.payment;

import java.time.LocalDate;

public class PaymentDto {

    public record CreatePlanRequest(
            String planName,
            Long monthlyPrice,
            Integer workspaceLimit,
            Integer memberLimit,
            String description
    ) {}

    // 구독 신청 요청
    public record SubscriptionRequest(
            Long planNo,
            String paymentMethod
    ) {}

    // Plan 제한
    public record PlanLimit(
            Integer memberLimit,
            Integer workspaceLimit
    ) {}


    // 결제 요청
    public record PaymentRequest(
            Long subscriptionNo,
            String paymentKey,
            String orderId,
            Long amount
    ) {}

    // 구독 취소 요청
    public record SubscriptionCancelRequest(
            String reason,
            Boolean refund
    ) {}


    // Plan 정보
    public static class PlanResponse{
        public Long planNo;
        public String planName; // Free, Pro
        public Long monthlyPrice;
        public Integer workSpaceLimit;
        public Integer memberLimit;
        public String description;
        public LocalDate createdDt;
    }


    // 구독 정보
    public static class SubscriptionResponse {
        public Long subscriptionNo;
        public Long memberNo;
        public Long planNo;
        public String planName;
        public String status;               // ACTIVE, PENDING, CANCELLED, EXPIRED
        public LocalDate startDt;
        public LocalDate renewalDt;         // 다음 갱신일
        public LocalDate cancelledDt;
        public String paymentMethod;
        public LocalDate createdDt;
        public LocalDate updatedDt;
    }

    // 결제 이력
    public static class PaymentHistoryResponse {
        public Long paymentNo;
        public Long subscriptionNo;
        public Long amount;
        public String status;               // SUCCESS, FAILED, PENDING
        public String paymentMethod;
        public String paymentKey;
        public String orderId;
        public String failureReason;        // 실패 사유
        public LocalDate paidDt;
        public LocalDate createdDt;
    }

    // 청구서
    public static class InvoiceResponse {
        public Long invoiceNo;
        public Long subscriptionNo;
        public Long memberNo;
        public Long amount;
        public String status;               // PENDING, PAID, OVERDUE, CANCELLED
        public LocalDate issuedDt;
        public LocalDate dueDt;
        public LocalDate paidDt;
        public String description;
        public LocalDate createdDt;
    }

    // 결제 준비 응답 (클라이언트에서 Toss Payments 결제 창 띄우기 위함)
    public static class PaymentPrepareResponse {
        public Long subscriptionNo;
        public String clientKey;            // Toss Payments 클라이언트 키
        public String orderId;              // 주문 ID
        public Long amount;
        public String orderName;            // 주문명
        public String customerName;
        public String customerEmail;
    }

    // ========== Flat DTO (MyBatis) ==========

    public record PlanFlatDto(
            Long planNo,
            String planName,
            Long monthlyPrice,
            Integer workspaceLimit,
            Integer memberLimit,
            String description,
            LocalDate createdDt
    ) {}

    public record SubscriptionFlatDto(
            Long subscriptionNo,
            Long memberNo,
            Long planNo,
            String planName,
            String status,
            LocalDate startDt,
            LocalDate renewalDt,
            LocalDate cancelledDt,
            String paymentMethod,
            LocalDate createdDt,
            LocalDate updatedDt
    ) {}

    public record PaymentHistoryFlatDto(
            Long paymentNo,
            Long subscriptionNo,
            Long amount,
            String status,
            String paymentMethod,
            String paymentKey,
            String orderId,
            String failureReason,
            LocalDate paidDt,
            LocalDate createdDt
    ) {}

    public record InvoiceFlatDto(
            Long invoiceNo,
            Long subscriptionNo,
            Long memberNo,
            Long amount,
            String status,
            LocalDate issuedDt,
            LocalDate dueDt,
            LocalDate paidDt,
            String description,
            LocalDate createdDt
    ) {}

}
