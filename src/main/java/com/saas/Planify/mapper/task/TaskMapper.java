package com.saas.Planify.mapper.task;

import com.saas.Planify.model.dto.task.TaskDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper {

    Long lastInsertId();

    void createTask(
            @Param("creatorNo") Long creatorNo,
            @Param("projectNo") Long projectNo,
            @Param("request") TaskDto.TaskCreateRequest request
    );

    // Task 완료
    void completeTask(
            Long taskNo
    );


    void createComment(
            @Param("taskNo") Long taskNo,
            @Param("memberNo") Long memberNo,
            @Param("request") TaskDto.TaskCommentCreateRequest request
    );


    void updateTask(
            @Param("taskNo") Long taskNo,
            @Param("request") TaskDto.TaskUpdateRequest request
    );


    // Task 순서 변경
    void updateTaskSortOrder(
            @Param("taskNo") Long taskNo,
            @Param("sortOrder") Integer sortOrder
    );


    void updateTaskStatus(
            @Param("taskNo") Long taskNo,
            @Param("request") TaskDto.TaskStatusUpdateRequest request
    );


    void updateComment(
            @Param("commentNo") Long commentNo,
            @Param("request") TaskDto.TaskCommentUpdateRequest request
    );


    void deleteTask(
            @Param("taskNo") Long taskNo
    );


    void deleteComment(
            @Param("commentNo") Long commentNo
    );


    // Task 삭제시 Comment 삭제
    void deleteTaskComment(
            Long taskNo
    );



    // 서브태스크 조회
    List<TaskDto.TaskFlatDto> getSubtasks(Long parentTaskNo);


    // Task 단건 조회
    TaskDto.TaskFlatDto singleTask(Long taskNo);


    // Project 내 Task 전체 조회
    List<TaskDto.TaskFlatDto> allTask(Long projectNo);


    // 생성자의 task 조회
    List<TaskDto.TaskFlatDto> creatorTasks(Long creatorNo);


    // 담당자의 task 조회 (미 완료)
    List<TaskDto.TaskFlatDto> AssigneeTask(Long assigneeNo);

    // Task 내 Comment 전체 조회
    List<TaskDto.TaskCommentFlatDto> allComment(Long taskNo);

    // comment 작성자 확인
    Long getCommentAuthor(Long commentNo);
}
