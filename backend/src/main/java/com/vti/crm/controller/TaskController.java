package com.vti.crm.controller;

import com.vti.crm.dto.request.TaskRequest;
import com.vti.crm.dto.response.TaskResponse;
import com.vti.crm.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    TaskResponse getTaskById(@PathVariable Integer id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    TaskResponse createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    TaskResponse updateTask(@PathVariable Integer id, @RequestBody TaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    String deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return "Task has been deleted";
    }
}
