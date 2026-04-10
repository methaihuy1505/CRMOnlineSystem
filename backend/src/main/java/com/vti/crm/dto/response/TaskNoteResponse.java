package com.vti.crm.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskNoteResponse {
    private Integer id;
    private Integer taskId;
    private Integer userId;
    private String content;
    private LocalDateTime createdAt;
}
