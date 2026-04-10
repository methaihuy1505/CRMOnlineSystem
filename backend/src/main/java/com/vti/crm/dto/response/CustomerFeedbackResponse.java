package com.vti.crm.dto.response;

import com.vti.crm.entity.CustomerFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedbackResponse {
    private Integer id;
    private Integer customerId;
    private Integer contactId;
    private String subject;
    private String content;
    private CustomerFeedback.Priority priority;
    private CustomerFeedback.Status status;
    private Integer assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}