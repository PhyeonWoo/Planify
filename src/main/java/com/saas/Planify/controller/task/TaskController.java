package com.saas.Planify.controller.task;

import com.saas.Planify.config.common.ApiResponse;
import com.saas.Planify.model.dto.task.TaskDto;
import com.saas.Planify.security.JwtProvider;
import com.saas.Planify.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {
    private final TaskService taskService;
    private final JwtProvider jwtProvider;

    @PostMapping("/{projectNo}")
    public ApiResponse<String> createTask(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long projectNo,
            @RequestBody TaskDto.TaskCreateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long creatorNo = jwtProvider.getMemberNo(token);

        taskService.createTask(projectNo, creatorNo, request);
        return ApiResponse.ok("생성 완료");
    }


    @PutMapping("/{taskNo}")
    public ApiResponse<String> updateTask(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo,
            @RequestBody TaskDto.TaskUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long creatorNo = jwtProvider.getMemberNo(token);

        taskService.updateTask(taskNo, creatorNo, request);
        return ApiResponse.ok("수정 완료");
    }


    @DeleteMapping("/{taskNo}")
    public ApiResponse<String> deleteTask(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long creatorNo = jwtProvider.getMemberNo(token);

        taskService.deleteTask(taskNo, creatorNo);
        return ApiResponse.ok("삭제 완료");
    }


    @GetMapping("/project/{projectNo}")
    public ApiResponse<List<TaskDto.TaskResponse>> allTask(
            @PathVariable Long projectNo
    ) {
        List<TaskDto.TaskResponse> response = taskService.allTask(projectNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/{taskNo}")
    public ApiResponse<TaskDto.TaskResponse> singleTask(
            @PathVariable Long taskNo
    ) {
        TaskDto.TaskResponse response = taskService.singleTask(taskNo);
        return ApiResponse.ok(response);
    }



    @GetMapping("/creator")
    public ApiResponse<List<TaskDto.TaskResponse>> creatorTasks(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long creatorNo = jwtProvider.getMemberNo(token);

        List<TaskDto.TaskResponse> response = taskService.creatorTasks(creatorNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/assignee")
    public ApiResponse<List<TaskDto.TaskResponse>> assigneeTasks(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long assigneeNo = jwtProvider.getMemberNo(token);

        List<TaskDto.TaskResponse> response = taskService.assigneeTasks(assigneeNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/{parentTaskNo}/subtasks")
    public ApiResponse<List<TaskDto.TaskResponse>> getSubTask(
            @PathVariable Long parentTaskNo
    ) {
        List<TaskDto.TaskResponse> response = taskService.getSubtasks(parentTaskNo);
        return ApiResponse.ok(response);
    }


    @GetMapping("/{taskNo}/comments")
    public ApiResponse<List<TaskDto.TaskCommentResponse>> taskComments(
            @PathVariable Long taskNo
    ) {
        List<TaskDto.TaskCommentResponse> response = taskService.taskComments(taskNo);
        return ApiResponse.ok(response);
    }


    @PostMapping("/{taskNo}/comments")
    public ApiResponse<String> createComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo,
            @RequestBody TaskDto.TaskCommentCreateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        taskService.createComment(taskNo, memberNo, request);
        return ApiResponse.ok("생성 완료");
    }


    @PutMapping("/comment/{commentNo}")
    public ApiResponse<String> updateComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long commentNo,
            @RequestBody TaskDto.TaskCommentUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        taskService.updateComment(commentNo, memberNo, request);
        return ApiResponse.ok("수정 완료");
    }


    @DeleteMapping("/comment/{commentNo}")
    public ApiResponse<String> deleteComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long commentNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        taskService.deleteComment(commentNo, memberNo);
        return ApiResponse.ok("삭제 완료");
    }


    @PatchMapping("/{taskNo}/status")
    public ApiResponse<String> updateTaskStatus(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo,
            @RequestBody TaskDto.TaskStatusUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        taskService.updateTaskStatus(taskNo, memberNo, request);
        return ApiResponse.ok("수정 완료");
    }

    @PatchMapping("/{taskNo}/complete")
    public ApiResponse<String> completeTask(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        taskService.completeTask(taskNo, memberNo);
        return ApiResponse.ok("완성으로 수정 완료");
    }


    @PatchMapping("/{taskNo}/sort-order")
    public ApiResponse<String> updateTaskSortOrder(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long taskNo,
            @RequestParam Integer sortOrder
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long creatorNo = jwtProvider.getMemberNo(token);

        taskService.updateTaskSortOrder(taskNo, creatorNo, sortOrder);
        return ApiResponse.ok("수정 완료");
    }

}
