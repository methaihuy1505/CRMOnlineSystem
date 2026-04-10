package com.vti.crm.dto.request;

import com.vti.crm.entity.User.Status;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private Integer roleId;
    private Integer branchId;
    private Integer teamId;
    private Status status;
}
