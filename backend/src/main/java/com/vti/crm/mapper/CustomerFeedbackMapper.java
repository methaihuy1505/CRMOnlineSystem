package com.vti.crm.mapper;

import com.vti.crm.dto.request.CustomerFeedbackRequest;
import com.vti.crm.dto.response.CustomerFeedbackResponse;
import com.vti.crm.entity.CustomerFeedback;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomerFeedbackMapper {
    public CustomerFeedback toEntity(CustomerFeedbackRequest request) {
        if (request == null) {
            return null;
        }
        return CustomerFeedback.builder()
                .customerId(request.getCustomerId())
                .contactId(request.getContactId())
                .subject(request.getSubject())
                .content(request.getContent())
                .priority(request.getPriority() != null ? request.getPriority() : CustomerFeedback.Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : CustomerFeedback.Status.OPEN)
                .assignedTo(request.getAssignedTo())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CustomerFeedbackResponse toResponse(CustomerFeedback entity) {
        if (entity == null) {
            return null;
        }
        return CustomerFeedbackResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .contactId(entity.getContactId())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .assignedTo(entity.getAssignedTo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}