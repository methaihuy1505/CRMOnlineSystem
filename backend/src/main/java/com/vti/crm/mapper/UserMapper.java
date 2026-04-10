package com.vti.crm.mapper;

import com.vti.crm.dto.request.UserRequest;
import com.vti.crm.dto.response.UserResponse;
import com.vti.crm.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .roleId(request.getRoleId())
                .branchId(request.getBranchId())
                .teamId(request.getTeamId())
                .status(request.getStatus())
                .build();
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;
        UserResponse response = new UserResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setFullName(entity.getFullName());
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setRoleId(entity.getRoleId());
        response.setBranchId(entity.getBranchId());
        response.setTeamId(entity.getTeamId());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public void updateEntity(User entity, UserRequest request) {
        if (request.getFullName() != null) entity.setFullName(request.getFullName());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getPhone() != null) entity.setPhone(request.getPhone());
        if (request.getRoleId() != null) entity.setRoleId(request.getRoleId());
        if (request.getBranchId() != null) entity.setBranchId(request.getBranchId());
        if (request.getTeamId() != null) entity.setTeamId(request.getTeamId());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
    }
}
