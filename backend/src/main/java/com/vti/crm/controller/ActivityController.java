package com.vti.crm.controller;

import com.vti.crm.dto.request.ActivityRequest;
import com.vti.crm.dto.response.ActivityResponse;
import com.vti.crm.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    List<ActivityResponse> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    ActivityResponse getActivityById(@PathVariable Integer id) {
        return activityService.getActivityById(id);
    }

    @PostMapping
    ActivityResponse createActivity(@RequestBody ActivityRequest request) {
        return activityService.createActivity(request);
    }

    @PutMapping("/{id}")
    ActivityResponse updateActivity(@PathVariable Integer id, @RequestBody ActivityRequest request) {
        return activityService.updateActivity(id, request);
    }

    @DeleteMapping("/{id}")
    String deleteActivity(@PathVariable Integer id) {
        activityService.deleteActivity(id);
        return "Activity has been deleted";
    }
}
