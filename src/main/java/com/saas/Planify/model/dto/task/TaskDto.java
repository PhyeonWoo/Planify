package com.saas.Planify.model.dto.task;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDto {

    // ============== Request ===============

    // Task 생성 요청
    public record TaskCreateRequest(
            String title,
            String description,
            TaskPriority priority,
            LocalDateTime dueDate,
            Integer sortOrder,
            Long assigneeNo,
            Long parentTaskNo
    ) {}

    // Task 수정 요청
    public record TaskUpdateRequest(
            String title,
            String description,
            TaskPriority priority,
            LocalDateTime dueDate,
            Integer sortOrder,
            Long assigneeNo
    ) {}

    // Task 상태 변경
    public record TaskStatusUpdateRequest(
            TaskStatus status
    ) {}

    // 댓글 생성
    public record TaskCommentCreateRequest(
            String content,
            Long parentCommentNo
    ) {}

    // 댓글 수정
    public record TaskCommentUpdateRequest(
            String content
    ) {}


    // ============== Response ===============

    // Task 상세 응답
    public static class TaskResponse {
        public Long taskNo;
        public Long projectNo;
        public String title;
        public String description;
        public String status;
        public String priority;
        public LocalDateTime dueDate;
        public Integer sortOrder;
        public Long creatorNo;
        public String creatorNickname;
        public Long assigneeNo;
        public String assigneeNickname;
        public Long parentTaskNo;
        public LocalDateTime createdDt;
        public LocalDateTime updatedDt;
        public LocalDateTime deletedDt;
        public LocalDateTime completedDt;
        public List<TaskCommentResponse> comments;
    }

    // Task 댓글 응답
    public static class TaskCommentResponse {
        public Long commentNo;
        public Long memberNo;
        public String memberNickname;
        public String content;
        public Long parentCommentNo;
        public LocalDateTime createdDt;
        public LocalDateTime updatedDt;
        public List<TaskCommentResponse> replies;
    }

    enum TaskStatus { TODO, IN_PROGRESS, DONE, CANCELLED }
    enum TaskPriority { URGENT, HIGH, MEDIUM, LOW }


    // ============== Flat DTO===============

    // Task 조회 결과 (Flat)
    public record TaskFlatDto(
            Long taskNo,
            Long projectNo,
            Long creatorNo,
            String creatorNickname,
            Long assigneeNo,
            String assigneeNickname,
            String title,
            String description,
            String status,
            String priority,
            LocalDateTime dueDate,
            Long parentTaskNo,
            Integer sortOrder,
            LocalDateTime createdDt,
            LocalDateTime updatedDt,
            LocalDateTime deletedDt,
            LocalDateTime completedDt
    ) {}


    // Task 댓글 조회 결과 (Flat)
    public record TaskCommentFlatDto(
            Long commentNo,
            Long taskNo,
            Long memberNo,
            String memberNickname,
            String content,
            Long parentCommentNo,
            LocalDateTime createdDt,
            LocalDateTime updatedDt,
            LocalDateTime deletedDt
    ) {}

}