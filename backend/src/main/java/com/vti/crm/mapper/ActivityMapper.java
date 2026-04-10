package com.vti.crm.mapper;

import com.vti.crm.dto.request.ActivityRequest;
import com.vti.crm.dto.response.ActivityResponse;
import com.vti.crm.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public Activity toEntity(ActivityRequest request) {
        if (request == null) return null;
        return Activity.builder()
                .parentId(request.getParentId())
                .parentType(request.getParentType())
                .contactId(request.getContactId())
                .activityType(request.getActivityType())
                .subject(request.getSubject())
                .description(request.getDescription())
                .duration(request.getDuration() != null ? request.getDuration() : 0)
                .activityDate(request.getActivityDate())
                .nextFollowUpDate(request.getNextFollowUpDate())
                .status(request.getStatus())
                .assignedTo(request.getAssignedTo())
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public ActivityResponse toResponse(Activity entity) {
        if (entity == null) return null;
        ActivityResponse response = new ActivityResponse();
        response.setId(entity.getId());
        if (entity.getTask() != null) {
            response.setTaskId(entity.getTask().getId());
        }
        response.setParentId(entity.getParentId());
        response.setParentType(entity.getParentType());
        response.setContactId(entity.getContactId());
        response.setActivityType(entity.getActivityType());
        response.setSubject(entity.getSubject());
        response.setDescription(entity.getDescription());
        response.setDuration(entity.getDuration());
        response.setActivityDate(entity.getActivityDate());
        response.setNextFollowUpDate(entity.getNextFollowUpDate());
        response.setStatus(entity.getStatus());
        response.setAssignedTo(entity.getAssignedTo());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public void updateEntity(Activity entity, ActivityRequest request) {
        if (request.getParentId() != null) entity.setParentId(request.getParentId());
        if (request.getParentType() != null) entity.setParentType(request.getParentType());
        if (request.getContactId() != null) entity.setContactId(request.getContactId());
        if (request.getActivityType() != null) entity.setActivityType(request.getActivityType());
        if (request.getSubject() != null) entity.setSubject(request.getSubject());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getDuration() != null) entity.setDuration(request.getDuration());
        if (request.getActivityDate() != null) entity.setActivityDate(request.getActivityDate());
        if (request.getNextFollowUpDate() != null) entity.setNextFollowUpDate(request.getNextFollowUpDate());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        if (request.getAssignedTo() != null) entity.setAssignedTo(request.getAssignedTo());
        if (request.getUpdatedBy() != null) entity.setUpdatedBy(request.getUpdatedBy());
    }
}
