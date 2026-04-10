package com.vti.crm.mapper;

import com.vti.crm.dto.request.TaskNoteRequest;
import com.vti.crm.dto.response.TaskNoteResponse;
import com.vti.crm.entity.TaskNote;
import org.springframework.stereotype.Component;

@Component
public class TaskNoteMapper {

    public TaskNote toEntity(TaskNoteRequest request) {
        if (request == null) return null;
        return TaskNote.builder()
                .userId(request.getUserId())
                .content(request.getContent())
                .build();
    }

    public TaskNoteResponse toResponse(TaskNote entity) {
        if (entity == null) return null;
        TaskNoteResponse response = new TaskNoteResponse();
        response.setId(entity.getId());
        if (entity.getTask() != null) {
            response.setTaskId(entity.getTask().getId());
        }
        response.setUserId(entity.getUserId());
        response.setContent(entity.getContent());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public void updateEntity(TaskNote entity, TaskNoteRequest request) {
        if (request.getUserId() != null) entity.setUserId(request.getUserId());
        if (request.getContent() != null) entity.setContent(request.getContent());
    }
}
