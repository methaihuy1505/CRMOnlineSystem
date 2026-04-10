package com.vti.crm.service;

import com.vti.crm.dto.request.UserRequest;
import com.vti.crm.dto.response.UserResponse;
import com.vti.crm.entity.User;
import com.vti.crm.exception.AppException;
import com.vti.crm.exception.ErrorCode;
import com.vti.crm.mapper.UserMapper;
import com.vti.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findByStatus(User.Status.ACTIVE).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        var user = userMapper.toEntity(request);
        if (user.getStatus() == null) {
            user.setStatus(User.Status.ACTIVE);
        }
        var savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest request) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateEntity(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Integer id) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }
}
