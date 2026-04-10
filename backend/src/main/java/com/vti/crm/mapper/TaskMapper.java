package com.vti.crm.mapper;

import com.vti.crm.dto.request.TaskRequest;
import com.vti.crm.dto.response.TaskResponse;
import com.vti.crm.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request) {
        if (request == null) return null;
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .extensionCount(request.getExtensionCount() != null ? request.getExtensionCount() : 0)
                .isOverdue(request.getIsOverdue() != null ? request.getIsOverdue() : false)
                .assignedTo(request.getAssignedTo())
                .relateType(request.getRelateType())
                .relateId(request.getRelateId())
                .createdBy(request.getCreatedBy())
                .build();
    }

    public TaskResponse toResponse(Task entity) {
        if (entity == null) return null;
        TaskResponse response = new TaskResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setPriority(entity.getPriority());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setExtensionCount(entity.getExtensionCount());
        response.setIsOverdue(entity.getIsOverdue());
        response.setAssignedTo(entity.getAssignedTo());
        response.setRelateType(entity.getRelateType());
        response.setRelateId(entity.getRelateId());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public void updateEntity(Task entity, TaskRequest request) {
        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getPriority() != null) entity.setPriority(request.getPriority());
        if (request.getStartDate() != null) entity.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) entity.setEndDate(request.getEndDate());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        if (request.getExtensionCount() != null) entity.setExtensionCount(request.getExtensionCount());
        if (request.getIsOverdue() != null) entity.setIsOverdue(request.getIsOverdue());
        if (request.getAssignedTo() != null) entity.setAssignedTo(request.getAssignedTo());
        if (request.getRelateType() != null) entity.setRelateType(request.getRelateType());
        if (request.getRelateId() != null) entity.setRelateId(request.getRelateId());
    }
}
