package com.vti.crm.dto.response;

import com.vti.crm.entity.User.Status;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Integer roleId;
    private Integer branchId;
    private Integer teamId;
    private Status status;
    private LocalDateTime createdAt;
}
