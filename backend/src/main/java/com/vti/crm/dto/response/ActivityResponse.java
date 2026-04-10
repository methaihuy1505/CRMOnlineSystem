package com.vti.crm.dto.response;

import com.vti.crm.entity.Activity.ActivityType;
import com.vti.crm.entity.Activity.ParentType;
import com.vti.crm.entity.Activity.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityResponse {
    private Integer id;
    private Integer taskId;
    private Integer parentId;
    private ParentType parentType;
    private Integer contactId;
    private ActivityType activityType;
    private String subject;
    private String description;
    private Integer duration;
    private LocalDateTime activityDate;
    private LocalDateTime nextFollowUpDate;
    private Status status;
    private Integer assignedTo;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
