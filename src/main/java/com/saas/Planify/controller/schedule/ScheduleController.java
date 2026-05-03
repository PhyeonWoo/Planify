package com.saas.Planify.controller.schedule;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.schedule.ScheduleDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtProvider jwtProvider;

    @PostMapping("/{workSpaceNo}")
    public ApiResponse<String> createSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long workSpaceNo,
            @RequestBody ScheduleDto.ScheduleCreateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        scheduleService.createSchedule(memberNo, workSpaceNo, request);
        return ApiResponse.ok("일정 생성 완료");
    }

    @PutMapping("/{scheduleNo}")
    public ApiResponse<String> updateSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long scheduleNo,
            @RequestBody ScheduleDto.ScheduleUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        scheduleService.updateSchedule(memberNo, scheduleNo, request);
        return ApiResponse.ok("일정 수정 완료");
    }

    @DeleteMapping("/{scheduleNo}")
    public ApiResponse<String> deleteSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long scheduleNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        scheduleService.deleteSchedule(scheduleNo, memberNo);
        return ApiResponse.ok("일정 삭제 완료");
    }

    @GetMapping("/{scheduleNo}")
    public ApiResponse<ScheduleDto.ScheduleResponse> getSchedule(
            @PathVariable Long scheduleNo
    ) {
        ScheduleDto.ScheduleResponse response = scheduleService.getSchedule(scheduleNo);
        return ApiResponse.ok(response);
    }

    @GetMapping("/my")
    public ApiResponse<List<ScheduleDto.ScheduleResponse>> getMySchedules(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        List<ScheduleDto.ScheduleResponse> responses = scheduleService.getMySchedules(memberNo);
        return ApiResponse.ok(responses);
    }

    @GetMapping("/workspace/{workSpaceNo}")
    public ApiResponse<List<ScheduleDto.ScheduleResponse>> getWorkSpaceSchedules(
            @PathVariable Long workSpaceNo
    ) {
        List<ScheduleDto.ScheduleResponse> responses = scheduleService.getWorkSpaceSchedules(workSpaceNo);
        return ApiResponse.ok(responses);
    }
}
