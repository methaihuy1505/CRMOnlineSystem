package com.vti.crm.controller;

import com.vti.crm.dto.request.TaskNoteRequest;
import com.vti.crm.dto.response.TaskNoteResponse;
import com.vti.crm.service.TaskNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-notes")
@RequiredArgsConstructor
public class TaskNoteController {

    private final TaskNoteService taskNoteService;

    @GetMapping("/task/{taskId}")
    List<TaskNoteResponse> getNotesByTaskId(@PathVariable Integer taskId) {
        return taskNoteService.getNotesByTaskId(taskId);
    }

    @PostMapping
    TaskNoteResponse createTaskNote(@RequestBody TaskNoteRequest request) {
        return taskNoteService.createTaskNote(request);
    }

    @PutMapping("/{id}")
    TaskNoteResponse updateTaskNote(@PathVariable Integer id, @RequestBody TaskNoteRequest request) {
        return taskNoteService.updateTaskNote(id, request);
    }

    @DeleteMapping("/task/{taskId}")
    String deleteNotesByTaskId(@PathVariable Integer taskId) {
        taskNoteService.deleteNotesByTaskId(taskId);
        return "Notes by Task ID have been deleted";
    }
}
