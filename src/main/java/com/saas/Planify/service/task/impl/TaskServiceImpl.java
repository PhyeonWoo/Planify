package com.saas.Planify.service.task.impl;

import com.saas.Planify.mapper.project.ProjectMapper;
import com.saas.Planify.mapper.task.TaskMapper;
import com.saas.Planify.model.dto.project.ProjectDto;
import com.saas.Planify.model.dto.task.TaskDto;
import com.saas.Planify.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;

    @Override
    public void createTask(Long projectNo, Long creatorNo, TaskDto.TaskCreateRequest request) {
        ProjectDto.ProjectFlatResponse response = projectMapper.singleProject(projectNo);
        if(response == null) {
            throw new IllegalArgumentException("Project가 존재하지 않습니다.");
        }

        ProjectDto.ProjectMemberFlatResponse member = projectMapper.singleProjectMember(projectNo, creatorNo);
        if(member == null) {
            throw new IllegalArgumentException("Project 내 member가 존재하지 않습니다.");
        }

        taskMapper.createTask(creatorNo, projectNo, request);
    }


    @Override
    public void updateTask(Long taskNo, Long creatorNo, TaskDto.TaskUpdateRequest request) {
        TaskDto.TaskFlatDto response = taskMapper.singleTask(taskNo);
        if(response == null) {
            throw new IllegalArgumentException("Task 가 존재하지 않습니다.");
        }

        if(!response.creatorNo().equals(creatorNo)) {
            throw new IllegalArgumentException("권한을 가진자만 수정이 가능합니다.");
        }

        taskMapper.updateTask(taskNo, request);
    }

    @Override
    public void updateTaskStatus(Long taskNo, Long memberNo, TaskDto.TaskStatusUpdateRequest request) {
        TaskDto.TaskFlatDto response = taskMapper.singleTask(taskNo);

        if(response == null) {
            throw new IllegalArgumentException("Task가 존재하지 않습니다.");
        }

        if(!response.creatorNo().equals(memberNo) && !response.assigneeNo().equals(memberNo)) {
            throw new IllegalArgumentException("권한을 가진자만 수정이 가능합니다.");
        }

        taskMapper.updateTaskStatus(taskNo, request);
    }

    @Override
    public void completeTask(Long taskNo, Long memberNo) {
        TaskDto.TaskFlatDto response = taskMapper.singleTask(taskNo);

        if(response == null) {
            throw new IllegalArgumentException("Task가 존재하지 않습니다.");
        }

        if(!response.creatorNo().equals(memberNo) && !response.assigneeNo().equals(memberNo)) {
            throw new IllegalArgumentException("권한을 가진자만 수정이 가능합니다.");
        }

        taskMapper.completeTask(taskNo);
    }

    @Override
    public void updateTaskSortOrder(Long taskNo, Long creatorNo, Integer sortOrder) {
        TaskDto.TaskFlatDto response = taskMapper.singleTask(taskNo);

        if(!response.creatorNo().equals(creatorNo)) {
            throw new IllegalArgumentException("권한을 가진자만 수정이 가능합니다.");
        }

        taskMapper.updateTaskSortOrder(taskNo, sortOrder);
    }



    @Override
    public void deleteTask(Long taskNo, Long creatorNo) {
        TaskDto.TaskFlatDto response = taskMapper.singleTask(taskNo);

        if(response == null) {
            throw new IllegalArgumentException("Task가 존재하지 않습니다.");
        }

        if(!response.creatorNo().equals(creatorNo)) {
            throw new IllegalArgumentException("권한을 가진자만 수정이 가능합니다.");
        }

        taskMapper.deleteTask(taskNo);
    }



    @Override
    public List<TaskDto.TaskResponse> allTask(Long projectNo) {
        return List.of();
    }

    @Override
    public TaskDto.TaskResponse singleTask(Long taskNo, Long creatorNo) {
        return null;
    }

    @Override
    public List<TaskDto.TaskResponse> creatorTasks(Long creatorNo) {
        return List.of();
    }

    @Override
    public List<TaskDto.TaskResponse> assigneeTasks(Long assigneeNo) {
        return List.of();
    }

    @Override
    public List<TaskDto.TaskResponse> getSubtasks(Long parentTaskNo) {
        return List.of();
    }

    @Override
    public void createComment(Long taskNo, Long memberNo, TaskDto.TaskCommentCreateRequest request) {

    }

    @Override
    public List<TaskDto.TaskCommentResponse> taskComments(Long taskNo) {
        return List.of();
    }

    @Override
    public void updateComment(Long commentNo, Long memberNo, TaskDto.TaskCommentUpdateRequest request) {

    }

    @Override
    public void deleteComment(Long commentNo, Long memberNo) {

    }
}
