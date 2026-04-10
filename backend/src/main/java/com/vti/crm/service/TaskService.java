package com.vti.crm.service;

import com.vti.crm.dto.request.TaskRequest;
import com.vti.crm.dto.response.TaskResponse;
import com.vti.crm.entity.Task;
import com.vti.crm.exception.AppException;
import com.vti.crm.exception.ErrorCode;
import com.vti.crm.mapper.TaskMapper;
import com.vti.crm.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findByStatusNot(Task.Status.CANCELED).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Integer id) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.TASK_NOT_FOUND));
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        var task = taskMapper.toEntity(request);
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.PENDING);
        }
        var savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.TASK_NOT_FOUND));
        taskMapper.updateEntity(task, request);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Integer id) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.TASK_NOT_FOUND));
        task.setStatus(Task.Status.CANCELED);
        taskRepository.save(task);
    }
}
