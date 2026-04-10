package com.vti.crm.service;

import com.vti.crm.dto.request.TaskNoteRequest;
import com.vti.crm.dto.response.TaskNoteResponse;
import com.vti.crm.exception.AppException;
import com.vti.crm.exception.ErrorCode;
import com.vti.crm.mapper.TaskNoteMapper;
import com.vti.crm.repository.TaskNoteRepository;
import com.vti.crm.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskNoteService {

    private final TaskNoteRepository taskNoteRepository;
    private final TaskRepository taskRepository;
    private final TaskNoteMapper taskNoteMapper;

    @Transactional(readOnly = true)
    public List<TaskNoteResponse> getNotesByTaskId(Integer taskId) {
        return taskNoteRepository.findByTaskId(taskId).stream()
                .map(taskNoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskNoteResponse createTaskNote(TaskNoteRequest request) {
        var taskNote = taskNoteMapper.toEntity(request);
        
        var task = taskRepository.findById(request.getTaskId()).orElseThrow(
                () -> new AppException(ErrorCode.TASK_NOT_FOUND));
        taskNote.setTask(task);
        
        var savedNote = taskNoteRepository.save(taskNote);
        return taskNoteMapper.toResponse(savedNote);
    }

    @Transactional
    public TaskNoteResponse updateTaskNote(Integer id, TaskNoteRequest request) {
        var taskNote = taskNoteRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.TASK_NOTE_NOT_FOUND));
        
        taskNoteMapper.updateEntity(taskNote, request);
        return taskNoteMapper.toResponse(taskNoteRepository.save(taskNote));
    }

    @Transactional
    public void deleteNotesByTaskId(Integer taskId) {
        taskNoteRepository.deleteByTaskId(taskId);
    }
}
