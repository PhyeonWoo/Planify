package com.saas.Planify.service.task.assembler;

import com.saas.Planify.model.dto.task.TaskDto;
import java.util.List;
import java.util.stream.Collectors;

public class TaskAssembler {


    public static TaskDto.TaskResponse toResponse(TaskDto.TaskFlatDto flat) {
        TaskDto.TaskResponse response = new TaskDto.TaskResponse();
        response.taskNo = flat.taskNo();
        response.projectNo = flat.projectNo();
        response.title = flat.title();
        response.description = flat.description();
        response.status = flat.status();
        response.priority = flat.priority();
        response.dueDate = flat.dueDate();
        response.sortOrder = flat.sortOrder();
        response.creatorNo = flat.creatorNo();
        response.creatorNickname = flat.creatorNickname();
        response.assigneeNo = flat.assigneeNo();
        response.assigneeNickname = flat.assigneeNickname();
        response.parentTaskNo = flat.parentTaskNo();
        response.createdDt = flat.createdDt();
        response.updatedDt = flat.updatedDt();
        response.deletedDt = flat.deletedDt();
        response.completedDt = flat.completedDt();
        response.comments = List.of();  // 별도로 조회

        return response;
    }

    // List<TaskFlatDto> → List<TaskResponse> 변환
    public static List<TaskDto.TaskResponse> toResponseList(List<TaskDto.TaskFlatDto> flats) {
        if (flats == null || flats.isEmpty()) {
            return List.of();
        }
        return flats.stream()
                .map(TaskAssembler::toResponse)
                .collect(Collectors.toList());
    }



    // TaskCommentFlatDto → TaskCommentResponse 변환
    public static TaskDto.TaskCommentResponse commentToResponse(TaskDto.TaskCommentFlatDto flat) {
        TaskDto.TaskCommentResponse response = new TaskDto.TaskCommentResponse();
        response.commentNo = flat.commentNo();
        response.memberNo = flat.memberNo();
        response.memberNickname = flat.memberNickname();
        response.content = flat.content();
        response.parentCommentNo = flat.parentCommentNo();
        response.createdDt = flat.createdDt();
        response.updatedDt = flat.updatedDt();
        response.replies = List.of();  // 대댓글은 별도 처리

        return response;
    }



    // List<TaskCommentFlatDto> → List<TaskCommentResponse> 변환
    public static List<TaskDto.TaskCommentResponse> commentToResponseList(List<TaskDto.TaskCommentFlatDto> flats) {
        if (flats == null || flats.isEmpty()) {
            return List.of();
        }

        return flats.stream()
                .map(TaskAssembler::commentToResponse)
                .collect(Collectors.toList());
    }
}