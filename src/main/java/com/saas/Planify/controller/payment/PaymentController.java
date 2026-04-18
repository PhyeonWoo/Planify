package com.saas.Planify.controller.payment;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.payment.PaymentDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final JwtProvider jwtProvider;

    // O
    @GetMapping("/plans")
    public ApiResponse<List<PaymentDto.PlanResponse>> getAllPlans() {
        List<PaymentDto.PlanResponse> response = paymentService.getAllPlans();
        return ApiResponse.ok(response);
    }

    // O
    @GetMapping("/plans/{planNo}")
    public ApiResponse<PaymentDto.PlanResponse> getPlan(
            @PathVariable Long planNo
    ) {
        PaymentDto.PlanResponse response = paymentService.getPlan(planNo);
        return ApiResponse.ok(response);
    }


    // Plan 생성
    @PostMapping("/create/plan")
    public ApiResponse<String> createPlan(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody PaymentDto.CreatePlanRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        paymentService.createPlan(memberNo, request);
        return ApiResponse.ok("생성 완료");
    }


    // O
    @PostMapping("/subscription")
    public ApiResponse<PaymentDto.PaymentPrepareResponse> createSubscription(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody PaymentDto.SubscriptionRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        PaymentDto.PaymentPrepareResponse response = paymentService.createSubscription(
                memberNo,
                request.planNo(),
                request.paymentMethod()
        );
        return ApiResponse.ok(response);
    }


    // 구독 확인

    // O
    @PostMapping("/subscription/confirm")
    public ApiResponse<String> confirmSubscription(
            @RequestBody PaymentDto.PaymentRequest request
    ) {
        paymentService.confirmSubscription(request);
        return ApiResponse.ok("활성화 되었습니다.");
    }


    // O
    @GetMapping("/subscription/active")
    public ApiResponse<PaymentDto.SubscriptionResponse> getActive(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        PaymentDto.SubscriptionResponse response = paymentService.getActiveSubscription(memberNo);
        return ApiResponse.ok(response);
    }

    // O
    @GetMapping("/subscription/history")
    public ApiResponse<List<PaymentDto.SubscriptionResponse>> subscriptionHistory(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<PaymentDto.SubscriptionResponse> response = paymentService.getSubscriptionHistory(memberNo);
        return ApiResponse.ok(response);
    }

    // O
    @DeleteMapping("/cancel/{subscriptionNo}")
    public ApiResponse<String> cancelSubscription(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long subscriptionNo,
            @RequestBody PaymentDto.SubscriptionCancelRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        paymentService.cancelSubscription(subscriptionNo, memberNo, request);
        return ApiResponse.ok("구독 취소 완료");
    }


    // 결제 이력 조회


//    @GetMapping("/success")
//    public ApiResponse<String> paymentSuccess(
//            @RequestParam String paymentKey,
//            @RequestParam String orderId,
//            @RequestParam Long amount
//    ) {
//        paymentService.processPaymentSuccess(paymentKey, orderId, amount);
//        return ApiResponse.ok("결제 성공");
//    }


    // Toss 결제 실패 리다이렉트 (failUrl)
    @GetMapping("/fail")
    public ApiResponse<String> paymentFail(
            @RequestParam String orderId,
            @RequestParam String message
    ) {
        paymentService.processPaymentFailure(orderId, message);
        return ApiResponse.ok("결제 실패 처리 완료");
    }



    // O
    @GetMapping("/history/{subscriptionNo}")
    public ApiResponse<List<PaymentDto.PaymentHistoryResponse>> paymentHistory(
            @PathVariable Long subscriptionNo,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<PaymentDto.PaymentHistoryResponse> response = paymentService.getPaymentHistory(subscriptionNo);
        return ApiResponse.ok(response);
    }

    // 사용자별 결제 이력 조회


    // O
    @GetMapping("/history")
    public ApiResponse<List<PaymentDto.PaymentHistoryResponse>> memberPaymentHistory(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<PaymentDto.PaymentHistoryResponse> response =
                paymentService.getMemberPaymentHistory(memberNo);
        return ApiResponse.ok(response);
    }


    // O
    @GetMapping("/invoice")
    public ApiResponse<List<PaymentDto.InvoiceResponse>> getInvoice(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<PaymentDto.InvoiceResponse> response = paymentService.getInvoices(memberNo);
        return ApiResponse.ok(response);
    }

    // O
    @GetMapping("/invoice/{subscriptionNo}")
    public ApiResponse<List<PaymentDto.InvoiceResponse>> subscriptionInvoice(
            @PathVariable Long subscriptionNo
    ) {
        List<PaymentDto.InvoiceResponse> response = paymentService.getSubscriptionInvoices(subscriptionNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/invoice/detail/{invoiceNo}")
    public ApiResponse<PaymentDto.InvoiceResponse> detailInvoice(
            @PathVariable Long invoiceNo
    ) {
        PaymentDto.InvoiceResponse response = paymentService.getInvoice(invoiceNo);
        return ApiResponse.ok(response);
    }
}

// invoice 부분 전체 안됌