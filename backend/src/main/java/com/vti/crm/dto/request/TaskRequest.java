package com.vti.crm.dto.request;

import com.vti.crm.entity.Task.Priority;
import com.vti.crm.entity.Task.RelateType;
import com.vti.crm.entity.Task.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;
    private Integer extensionCount;
    private Boolean isOverdue;
    private Integer assignedTo;
    private RelateType relateType;
    private Integer relateId;
    private Integer createdBy;
}
