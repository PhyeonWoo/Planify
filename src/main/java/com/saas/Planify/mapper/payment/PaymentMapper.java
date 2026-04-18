package com.saas.Planify.mapper.payment;

import com.saas.Planify.model.dto.payment.PaymentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentMapper {

    // ========== Plan (플랜) ==========

    /**
     * 모든 플랜 조회
     */
    List<PaymentDto.PlanFlatDto> allPlans();

    /**
     * 단일 플랜 조회
     */
    PaymentDto.PlanFlatDto singlePlan(Long planNo);

    /**
     * 플랜 생성 (관리자)
     */
    int createPlan(PaymentDto.CreatePlanRequest request);

    // ========== Subscription (구독) ==========

    /**
     * 구독 생성
     */
    int createSubscription(@Param("memberNo") Long memberNo,
                           @Param("planNo") Long planNo,
                           @Param("paymentMethod") String paymentMethod);

    /**
     * 사용자의 활성 구독 조회
     */
    PaymentDto.SubscriptionFlatDto activeSubscription(Long memberNo);

    /**
     * 단일 구독 조회
     */
    PaymentDto.SubscriptionFlatDto singleSubscription(Long subscriptionNo);

    /**
     * 사용자의 모든 구독 이력
     */
    List<PaymentDto.SubscriptionFlatDto> memberSubscriptions(Long memberNo);

    /**
     * 구독 상태 업데이트
     */
    int updateSubscriptionStatus(@Param("subscriptionNo") Long subscriptionNo,
                                 @Param("status") String status);

    /**
     * 구독 취소
     */
    int cancelSubscription(@Param("subscriptionNo") Long subscriptionNo);


    // ========== Payment (결제 이력) ==========

    /**
     * 결제 이력 생성
     */
    int createPaymentHistory(@Param("subscriptionNo") Long subscriptionNo,
                             @Param("amount") Long amount,
                             @Param("status") String status,
                             @Param("paymentMethod") String paymentMethod,
                             @Param("paymentKey") String paymentKey,
                             @Param("orderId") String orderId);

    /**
     * 결제 이력 조회 (구독별)
     */
    List<PaymentDto.PaymentHistoryFlatDto> subscriptionPayments(Long subscriptionNo);

    /**
     * 단일 결제 이력 조회
     */
    PaymentDto.PaymentHistoryFlatDto singlePayment(Long paymentNo);

    /**
     * 결제 상태 업데이트
     */
    int updatePaymentStatus(@Param("paymentNo") Long paymentNo,
                            @Param("status") String status,
                            @Param("paymentKey") String paymentKey);

    /**
     * 결제 실패 기록
     */
    int recordPaymentFailure(@Param("paymentNo") Long paymentNo,
                             @Param("failureReason") String failureReason);

    /**
     * 사용자의 결제 이력
     */
    List<PaymentDto.PaymentHistoryFlatDto> memberPaymentHistory(Long memberNo);

    // ========== Invoice (청구서) ==========

    /**
     * 청구서 생성
     */
    int createInvoice(@Param("subscriptionNo") Long subscriptionNo,
                      @Param("memberNo") Long memberNo,
                      @Param("amount") Long amount,
                      @Param("description") String description);

    /**
     * 사용자의 청구서 조회
     */
    List<PaymentDto.InvoiceFlatDto> memberInvoices(Long memberNo);

    /**
     * 구독의 청구서 조회
     */
    List<PaymentDto.InvoiceFlatDto> subscriptionInvoices(Long subscriptionNo);

    /**
     * 단일 청구서 조회
     */
    PaymentDto.InvoiceFlatDto singleInvoice(Long invoiceNo);

    // ========== Last Insert ID ==========

    // 현재 워스크페이스 내 멤버 수 조회
    int currentWorkSpaceMember(Long workSpaceNo);

    // 현재 워크스페이스 수 조회
    int countMemberWorkspace(Long memberNo);

    // 활성 플랜 한도 조회
    PaymentDto.PlanLimit getMemberPlanLimit(Long memberNo);

    Long lastInsertId();
}