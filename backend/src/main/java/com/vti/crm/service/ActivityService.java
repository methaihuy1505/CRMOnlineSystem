package com.vti.crm.service;

import com.vti.crm.dto.request.ActivityRequest;
import com.vti.crm.dto.response.ActivityResponse;
import com.vti.crm.entity.Activity;
import com.vti.crm.exception.AppException;
import com.vti.crm.exception.ErrorCode;
import com.vti.crm.mapper.ActivityMapper;
import com.vti.crm.repository.ActivityRepository;
import com.vti.crm.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TaskRepository taskRepository;
    private final ActivityMapper activityMapper;

    @Transactional(readOnly = true)
    public List<ActivityResponse> getAllActivities() {
        return activityRepository.findByStatusNot(Activity.Status.NOT_HELD).stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActivityResponse getActivityById(Integer id) {
        var activity = activityRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.ACTIVITY_NOT_FOUND));
        return activityMapper.toResponse(activity);
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        var activity = activityMapper.toEntity(request);
        
        if (request.getTaskId() != null) {
            var task = taskRepository.findById(request.getTaskId()).orElseThrow(
                    () -> new AppException(ErrorCode.TASK_NOT_FOUND));
            activity.setTask(task);
        }

        if (activity.getStatus() == null) {
            activity.setStatus(Activity.Status.PLANNED);
        }
        
        var savedActivity = activityRepository.save(activity);
        return activityMapper.toResponse(savedActivity);
    }

    @Transactional
    public ActivityResponse updateActivity(Integer id, ActivityRequest request) {
        var activity = activityRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.ACTIVITY_NOT_FOUND));
        
        activityMapper.updateEntity(activity, request);
        
        if (request.getTaskId() != null && (activity.getTask() == null || !activity.getTask().getId().equals(request.getTaskId()))) {
            var task = taskRepository.findById(request.getTaskId()).orElseThrow(
                    () -> new AppException(ErrorCode.TASK_NOT_FOUND));
            activity.setTask(task);
        }
        
        activity.setUpdatedAt(LocalDateTime.now());
        return activityMapper.toResponse(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Integer id) {
        var activity = activityRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.ACTIVITY_NOT_FOUND));
        activity.setStatus(Activity.Status.NOT_HELD);
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }
}
