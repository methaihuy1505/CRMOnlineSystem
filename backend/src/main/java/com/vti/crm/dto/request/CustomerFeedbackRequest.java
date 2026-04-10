package com.vti.crm.dto.request;

import com.vti.crm.entity.CustomerFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedbackRequest {
    private Integer customerId;
    private Integer contactId;
    private String subject;
    private String content;
    private CustomerFeedback.Priority priority;
    private CustomerFeedback.Status status;
    private Integer assignedTo;
}