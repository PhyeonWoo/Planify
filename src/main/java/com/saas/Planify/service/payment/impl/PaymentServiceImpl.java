package com.saas.Planify.service.payment.impl;

import com.saas.Planify.mapper.auth.AuthMapper;
import com.saas.Planify.mapper.payment.PaymentMapper;
import com.saas.Planify.model.dto.auth.AuthDto;
import com.saas.Planify.model.dto.payment.PaymentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.Planify.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final AuthMapper authMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.env:dev}")
    private String appEnv;

    @Value("${toss.payments.secret-key}")
    private String tossSecretKey;

    @Value("${toss.payments.client-key}")
    private String tossClientKey;

    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments";


    // ========== Plan (플랜) ==========

    @Override
    @Cacheable(value = "plans", key = "'all'")
    public List<PaymentDto.PlanResponse> getAllPlans() {
        log.info("[Cache MISS] DB에서 플랜 목록 조회");
        List<PaymentDto.PlanFlatDto> flats = paymentMapper.allPlans();
        return flats.stream()
                .map(this::convertPlanFlatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "plans", key = "#planNo")
    public PaymentDto.PlanResponse getPlan(Long planNo) {
        log.info("[Cache MISS] DB에서 플랜 조회: planNo={}", planNo);
        PaymentDto.PlanFlatDto flat = paymentMapper.singlePlan(planNo);
        if (flat == null) {
            throw new IllegalArgumentException("플랜이 없습니다");
        }
        return convertPlanFlatToResponse(flat);
    }

    @Override
    @Transactional
    @CacheEvict(value = "plans", allEntries = true)
    public void createPlan(Long memberNo, PaymentDto.CreatePlanRequest request) {
        AuthDto.LoginInfoResponse response = authMapper.findByMemberNo(memberNo);
        if (!"ADMIN".equals(response.role())) {
            throw new IllegalArgumentException("ADMIN만 생성 가능합니다.");
        }
        int count = paymentMapper.createPlan(request);
        if (count == 0) {
            throw new IllegalArgumentException("생성 안됨");
        }
        log.info("[Cache EVICT] 플랜 생성으로 plans 캐시 전체 무효화");
    }


    // ========== Subscription (구독) ==========

    @Override
    @Transactional
    public PaymentDto.PaymentPrepareResponse createSubscription(
            Long memberNo,
            Long planNo,
            String paymentMethod
    ) {
        PaymentDto.PlanFlatDto plan = paymentMapper.singlePlan(planNo);
        if (plan == null) {
            throw new IllegalArgumentException("플랜이 없습니다");
        }

        PaymentDto.SubscriptionFlatDto existingSubscription = paymentMapper.activeSubscription(memberNo);
        if (existingSubscription != null && !"CANCELLED".equals(existingSubscription.status())) {
            throw new IllegalArgumentException("이미 활성 구독이 있습니다. 먼저 취소해주세요.");
        }

        int count = paymentMapper.createSubscription(memberNo, planNo, paymentMethod);
        if (count == 0) {
            throw new IllegalArgumentException("구독 생성에 실패했습니다");
        }

        PaymentDto.SubscriptionFlatDto subscription = paymentMapper.activeSubscription(memberNo);
        Long subscriptionNo = subscription.subscriptionNo();

        String orderId = generateOrderId(subscriptionNo);
        String orderName = "Planify " + plan.planName() + " 플랜 구독";

        PaymentDto.PaymentPrepareResponse response = new PaymentDto.PaymentPrepareResponse();
        response.subscriptionNo = subscriptionNo;
        response.clientKey = tossClientKey;
        response.orderId = orderId;
        response.amount = plan.monthlyPrice();
        response.orderName = orderName;
        response.customerName = "User" + memberNo;
        response.customerEmail = "member" + memberNo + "@planify.com";

        return response;
    }

    @Override
    @Transactional
    public void confirmSubscription(PaymentDto.PaymentRequest request) {
        PaymentDto.SubscriptionFlatDto subscription = paymentMapper.singleSubscription(request.subscriptionNo());
        if (subscription == null) {
            throw new IllegalArgumentException("구독이 없습니다");
        }
        if (!"PENDING".equals(subscription.status())) {
            throw new IllegalArgumentException("대기 중인 구독이 아닙니다");
        }

        try {
            verifyPaymentWithToss(request.paymentKey(), request.orderId(), request.amount());

            int count = paymentMapper.createPaymentHistory(
                    request.subscriptionNo(),
                    request.amount(),
                    "SUCCESS",
                    subscription.paymentMethod(),
                    request.paymentKey(),
                    request.orderId()
            );
            if (count == 0) {
                throw new IllegalArgumentException("결제 이력 저장에 실패했습니다");
            }

            paymentMapper.updateSubscriptionStatus(request.subscriptionNo(), "ACTIVE");

            PaymentDto.PlanFlatDto plan = paymentMapper.singlePlan(subscription.planNo());
            paymentMapper.createInvoice(
                    request.subscriptionNo(),
                    subscription.memberNo(),
                    request.amount(),
                    plan.planName() + " 플랜 월간 구독료"
            );

            log.info("구독 확인 완료: subscriptionNo={}, memberNo={}",
                    request.subscriptionNo(), subscription.memberNo());

        } catch (Exception e) {
            log.error("결제 확인 실패: {}", e.getMessage());

            PaymentDto.PaymentHistoryFlatDto payment = paymentMapper.singlePayment(request.subscriptionNo());
            if (payment != null) {
                paymentMapper.recordPaymentFailure(payment.paymentNo(), e.getMessage());
            }

            paymentMapper.cancelSubscription(request.subscriptionNo());

            throw new IllegalArgumentException("결제 검증에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancelSubscription(Long subscriptionNo, Long memberNo, PaymentDto.SubscriptionCancelRequest request) {
        PaymentDto.SubscriptionFlatDto subscription = paymentMapper.singleSubscription(subscriptionNo);
        if (subscription == null) {
            throw new IllegalArgumentException("구독이 없습니다");
        }
        if (!subscription.memberNo().equals(memberNo)) {
            throw new IllegalArgumentException("취소 권한이 없습니다");
        }
        if ("CANCELLED".equals(subscription.status())) {
            throw new IllegalArgumentException("이미 취소된 구독입니다");
        }

        paymentMapper.cancelSubscription(subscriptionNo);

        if (request.refund()) {
            processRefund(subscriptionNo, "사용자 요청: " + request.reason());
        }

        log.info("구독 취소: subscriptionNo={}, reason={}", subscriptionNo, request.reason());
    }




    @Override
    public List<PaymentDto.SubscriptionResponse> getSubscriptionHistory(Long memberNo) {
        List<PaymentDto.SubscriptionFlatDto> flats = paymentMapper.memberSubscriptions(memberNo);
        return flats.stream()
                .map(this::convertSubscriptionFlatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto.SubscriptionResponse getActiveSubscription(Long memberNo) {
        PaymentDto.SubscriptionFlatDto flat = paymentMapper.activeSubscription(memberNo);
        if (flat == null) {
            return null;
        }
        return convertSubscriptionFlatToResponse(flat);
    }


    // ========== Payment (결제) ==========

    @Override
    public List<PaymentDto.PaymentHistoryResponse> getPaymentHistory(Long subscriptionNo) {
        List<PaymentDto.PaymentHistoryFlatDto> flats = paymentMapper.subscriptionPayments(subscriptionNo);
        return flats.stream()
                .map(this::convertPaymentFlatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto.PaymentHistoryResponse> getMemberPaymentHistory(Long memberNo) {
        List<PaymentDto.PaymentHistoryFlatDto> flats = paymentMapper.memberPaymentHistory(memberNo);
        return flats.stream()
                .map(this::convertPaymentFlatToResponse)
                .collect(Collectors.toList());
    }


    // 결제 성공
//    @Override
//    @Transactional
//    public void processPaymentSuccess(String paymentKey, String orderId, Long amount) {
//        Long subscriptionNo = extractSubscriptionNoFromOrderId(orderId);
//
//        PaymentDto.SubscriptionFlatDto subscription = paymentMapper.singleSubscription(subscriptionNo);
//        if (subscription == null) {
//            throw new IllegalArgumentException("구독이 없습니다");
//        }
//
//        paymentMapper.createPaymentHistory(
//                subscriptionNo, amount, "SUCCESS",
//                subscription.paymentMethod(), paymentKey, orderId
//        );
//
//        paymentMapper.updateSubscriptionStatus(subscriptionNo, "ACTIVE");
//
//        PaymentDto.PlanFlatDto plan = paymentMapper.singlePlan(subscription.planNo());
//        paymentMapper.createInvoice(
//                subscriptionNo, subscription.memberNo(), amount,
//                plan.planName() + " 플랜 월간 구독료"
//        );
//
//        log.info("결제 성공 처리: paymentKey={}, subscriptionNo={}", paymentKey, subscriptionNo);
//    }



    // 결제 실패
    @Override
    @Transactional
    public void processPaymentFailure(String orderId, String failureReason) {
        Long subscriptionNo = extractSubscriptionNoFromOrderId(orderId);

        PaymentDto.SubscriptionFlatDto subscription = paymentMapper.singleSubscription(subscriptionNo);
        if (subscription == null) {
            throw new IllegalArgumentException("구독이 없습니다");
        }

        paymentMapper.cancelSubscription(subscriptionNo);
        log.warn("결제 실패 처리: subscriptionNo={}, reason={}", subscriptionNo, failureReason);
    }


    // ========== Invoice (청구서) ==========

    @Override
    public List<PaymentDto.InvoiceResponse> getInvoices(Long memberNo) {
        List<PaymentDto.InvoiceFlatDto> flats = paymentMapper.memberInvoices(memberNo);
        return flats.stream()
                .map(this::convertInvoiceFlatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto.InvoiceResponse> getSubscriptionInvoices(Long subscriptionNo) {
        List<PaymentDto.InvoiceFlatDto> flats = paymentMapper.subscriptionInvoices(subscriptionNo);
        return flats.stream()
                .map(this::convertInvoiceFlatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto.InvoiceResponse getInvoice(Long invoiceNo) {
        PaymentDto.InvoiceFlatDto flat = paymentMapper.singleInvoice(invoiceNo);
        if (flat == null) {
            throw new IllegalArgumentException("청구서가 없습니다");
        }
        return convertInvoiceFlatToResponse(flat);
    }

    private static final int FREE_WORKSPACE_LIMIT = 1;
    private static final int FREE_MEMBER_LIMIT = 3;

    @Override
    public void checkWorkSpaceLimit(Long memberNo) {
        PaymentDto.PlanLimit limit = paymentMapper.getMemberPlanLimit(memberNo);
        int workspaceLimit = (limit != null) ? limit.workspaceLimit() : FREE_WORKSPACE_LIMIT;

        int count = paymentMapper.countMemberWorkspace(memberNo);
        if (count >= workspaceLimit) {
            throw new IllegalArgumentException(
                limit != null
                    ? "워크스페이스 한도를 초과했습니다. (한도: " + workspaceLimit + "개)"
                    : "무료 플랜은 워크스페이스를 " + FREE_WORKSPACE_LIMIT + "개까지만 생성할 수 있습니다. 플랜을 업그레이드해주세요."
            );
        }
    }

    @Override
    public void checkMemberLimit(Long workSpaceNo, Long memberNo) {
        PaymentDto.PlanLimit limit = paymentMapper.getMemberPlanLimit(memberNo);
        int memberLimit = (limit != null) ? limit.memberLimit() : FREE_MEMBER_LIMIT;

        int count = paymentMapper.currentWorkSpaceMember(workSpaceNo);
        if (count >= memberLimit) {
            throw new IllegalArgumentException(
                limit != null
                    ? "멤버 한도를 초과했습니다. (한도: " + memberLimit + "명)"
                    : "무료 플랜은 멤버를 " + FREE_MEMBER_LIMIT + "명까지만 초대할 수 있습니다. 플랜을 업그레이드해주세요."
            );
        }
    }


    // ========== Private Helper Methods ==========



    private Map<String, Object> verifyPaymentWithToss(String paymentKey, String orderId, Long amount) {
        try {
            if ("dev".equals(appEnv) || "test".equals(appEnv)) {
                log.info("Mock 결제 검증 (개발 환경): paymentKey={}, orderId={}, amount={}",
                        paymentKey, orderId, amount);

                Map<String, Object> mockResponse = new HashMap<>();
                mockResponse.put("paymentKey", paymentKey);
                mockResponse.put("orderId", orderId);
                mockResponse.put("amount", amount);
                mockResponse.put("status", "DONE");
                mockResponse.put("method", "CARD");
                return mockResponse;
            }

            String auth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + auth);
            headers.set("Content-Type", "application/json");

            String requestBody = "{\"paymentKey\":\"" + paymentKey + "\",\"orderId\":\"" + orderId + "\",\"amount\":" + amount + "}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    TOSS_PAYMENT_URL + "/" + paymentKey,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("결제 검증 실패");
            }

            return objectMapper.readValue(response.getBody(), Map.class);

        } catch (Exception e) {
            throw new IllegalArgumentException("Toss Payments API 오류: " + e.getMessage());
        }
    }





    private void processRefund(Long subscriptionNo, String reason) {
        PaymentDto.PaymentHistoryFlatDto payment = paymentMapper.singlePayment(subscriptionNo);
        if (payment == null || payment.paymentKey() == null) {
            log.warn("환불할 결제 내역 없음: subscriptionNo={}", subscriptionNo);
            return;
        }

        try {
            if ("dev".equals(appEnv) || "test".equals(appEnv)) {
                log.info("Mock 환불 처리 (개발 환경): subscriptionNo={}, reason={}", subscriptionNo, reason);
                return;
            }

            String auth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + auth);
            headers.set("Content-Type", "application/json");

            String requestBody = "{\"cancelReason\":\"" + reason + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    TOSS_PAYMENT_URL + "/" + payment.paymentKey() + "/cancel",
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("환불 요청 실패");
            }

            log.info("환불 완료: subscriptionNo={}, paymentKey={}", subscriptionNo, payment.paymentKey());

        } catch (Exception e) {
            log.error("환불 처리 오류: {}", e.getMessage());
            throw new IllegalArgumentException("환불 처리 실패: " + e.getMessage());
        }
    }







    private String generateOrderId(Long subscriptionNo) {
        return "ORD-" + subscriptionNo + "-" + System.currentTimeMillis();
    }



    private Long extractSubscriptionNoFromOrderId(String orderId) {
        String[] parts = orderId.split("-");
        if (parts.length < 3) {
            throw new IllegalArgumentException("잘못된 주문 ID: " + orderId);
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("주문 ID 파싱 오류");
        }
    }


    // ========== Converter Methods ==========

    private PaymentDto.PlanResponse convertPlanFlatToResponse(PaymentDto.PlanFlatDto flat) {
        PaymentDto.PlanResponse response = new PaymentDto.PlanResponse();
        response.planNo = flat.planNo();
        response.planName = flat.planName();
        response.monthlyPrice = flat.monthlyPrice();
        response.memberLimit = flat.memberLimit();
        response.workSpaceLimit = flat.workspaceLimit();
        response.description = flat.description();
        response.createdDt = flat.createdDt();
        return response;
    }

    private PaymentDto.SubscriptionResponse convertSubscriptionFlatToResponse(PaymentDto.SubscriptionFlatDto flat) {
        PaymentDto.SubscriptionResponse response = new PaymentDto.SubscriptionResponse();
        response.subscriptionNo = flat.subscriptionNo();
        response.memberNo = flat.memberNo();
        response.planNo = flat.planNo();
        response.planName = flat.planName();
        response.status = flat.status();
        response.startDt = flat.startDt();
        response.renewalDt = flat.renewalDt();
        response.cancelledDt = flat.cancelledDt();
        response.paymentMethod = flat.paymentMethod();
        response.createdDt = flat.createdDt();
        response.updatedDt = flat.updatedDt();
        return response;
    }

    private PaymentDto.PaymentHistoryResponse convertPaymentFlatToResponse(PaymentDto.PaymentHistoryFlatDto flat) {
        PaymentDto.PaymentHistoryResponse response = new PaymentDto.PaymentHistoryResponse();
        response.paymentNo = flat.paymentNo();
        response.subscriptionNo = flat.subscriptionNo();
        response.amount = flat.amount();
        response.status = flat.status();
        response.paymentMethod = flat.paymentMethod();
        response.paymentKey = flat.paymentKey();
        response.orderId = flat.orderId();
        response.failureReason = flat.failureReason();
        response.paidDt = flat.paidDt();
        response.createdDt = flat.createdDt();
        return response;
    }

    private PaymentDto.InvoiceResponse convertInvoiceFlatToResponse(PaymentDto.InvoiceFlatDto flat) {
        PaymentDto.InvoiceResponse response = new PaymentDto.InvoiceResponse();
        response.invoiceNo = flat.invoiceNo();
        response.subscriptionNo = flat.subscriptionNo();
        response.memberNo = flat.memberNo();
        response.amount = flat.amount();
        response.status = flat.status();
        response.issuedDt = flat.issuedDt();
        response.dueDt = flat.dueDt();
        response.paidDt = flat.paidDt();
        response.description = flat.description();
        response.createdDt = flat.createdDt();
        return response;
    }
}