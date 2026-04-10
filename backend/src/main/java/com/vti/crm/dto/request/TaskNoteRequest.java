package com.vti.crm.dto.request;

import lombok.Data;

@Data
public class TaskNoteRequest {
    private Integer taskId;
    private Integer userId;
    private String content;
}
