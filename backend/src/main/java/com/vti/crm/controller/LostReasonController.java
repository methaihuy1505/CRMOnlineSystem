package com.vti.crm.controller;

import com.vti.crm.dto.request.LostReasonRequest;
import com.vti.crm.dto.response.LostReasonResponse;
import com.vti.crm.service.LostReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lost-reasons")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LostReasonController {

    private final LostReasonService service;

    @GetMapping
    public List<LostReasonResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public LostReasonResponse getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public LostReasonResponse create(@RequestBody LostReasonRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public LostReasonResponse update(
            @PathVariable Integer id,
            @RequestBody LostReasonRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}