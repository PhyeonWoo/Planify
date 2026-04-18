package com.saas.Planify.service.payment;

import com.saas.Planify.model.dto.payment.PaymentDto;
import java.util.List;

public interface PaymentService {

    // ========== Plan (플랜) ==========


    // plan 종류 전체 조회
    List<PaymentDto.PlanResponse> getAllPlans();


    // plan 전체 조회
    PaymentDto.PlanResponse getPlan(Long planNo);

    // ========== Subscription (구독) ==========


    // plan 생성 (ADMIN 만)
    void createPlan(Long memberNo, PaymentDto.CreatePlanRequest request);


    // 구독 생성
    PaymentDto.PaymentPrepareResponse createSubscription(
            Long memberNo,
            Long planNo,
            String paymentMethod
    );



    // 내 구독 활성상태 조회
    PaymentDto.SubscriptionResponse getActiveSubscription(Long memberNo);


    // 결제 후 구독확인 조회
    void confirmSubscription(PaymentDto.PaymentRequest request);


    // 구독 취소
    void cancelSubscription(Long subscriptionNo, Long memberNo, PaymentDto.SubscriptionCancelRequest request);


    // 구독 이력 조회
    List<PaymentDto.SubscriptionResponse> getSubscriptionHistory(Long memberNo);

    // ========== Payment (결제) ==========


    // 결제 이력 조회
    List<PaymentDto.PaymentHistoryResponse> getPaymentHistory(Long subscriptionNo);


    // 사용자 결제 이력 조회
    List<PaymentDto.PaymentHistoryResponse> getMemberPaymentHistory(Long memberNo);


    // 결제 성공 처리
//    void processPaymentSuccess(String paymentKey, String orderId, Long amount);



    // 결제 실패 처리
    void processPaymentFailure(String orderId, String failureReason);

    // ========== Invoice (청구서) ==========


    // 사용자 청구서 조회
    List<PaymentDto.InvoiceResponse> getInvoices(Long memberNo);


    // 구독 청구서 조회
    List<PaymentDto.InvoiceResponse> getSubscriptionInvoices(Long subscriptionNo);


    // 청구서 상세 조회
    PaymentDto.InvoiceResponse getInvoice(Long invoiceNo);


    void checkWorkSpaceLimit(Long memberNo);

    void checkMemberLimit(Long workSpaceNo, Long memberNo);

}