package com.saas.Planify.service.task;

import com.saas.Planify.model.dto.task.TaskDto;

import java.util.List;

public interface TaskService {

    // ================ TASK ==================

    // Task 생성
    void createTask(
            Long projectNo,
            Long creatorNo,
            TaskDto.TaskCreateRequest request
    );

    // Task 수정
    void updateTask(
            Long taskNo,
            Long creatorNo,
            TaskDto.TaskUpdateRequest request
    );

    // Task 상태 변경
    void updateTaskStatus(
            Long taskNo,
            Long memberNo,
            TaskDto.TaskStatusUpdateRequest request
    );

    // Task 완료
    void completeTask(
            Long taskNo,
            Long memberNo
    );

    // Task 순서 변경
    void updateTaskSortOrder(
            Long taskNo,
            Long memberNo,
            Integer sortOrder
    );

    // Task 삭제
    void deleteTask(
            Long taskNo,
            Long creatorNo
    );

    // 프로젝트의 모든 Task 조회
    List<TaskDto.TaskResponse> allTask(
            Long projectNo
    );

    // Task 단건 조회
    TaskDto.TaskResponse singleTask(
            Long taskNo,
            Long creatorNo
    );

    // 생성자의 Task 조회
    List<TaskDto.TaskResponse> creatorTasks(
            Long creatorNo
    );

    // 담당자 Task 조회 (미완료)
    List<TaskDto.TaskResponse> assigneeTasks(
            Long assigneeNo
    );

    // 서브태스크 조회
    List<TaskDto.TaskResponse> getSubtasks(
            Long parentTaskNo
    );


    // ================ Comment ==================

    // 댓글 생성
    void createComment(
            Long taskNo,
            Long memberNo,
            TaskDto.TaskCommentCreateRequest request
    );

    // Task의 모든 댓글 조회
    List<TaskDto.TaskCommentResponse> taskComments(
            Long taskNo
    );

    // 댓글 수정
    void updateComment(
            Long commentNo,
            Long memberNo,
            TaskDto.TaskCommentUpdateRequest request
    );

    // 댓글 삭제
    void deleteComment(
            Long commentNo,
            Long memberNo
    );
}