package com.vti.crm.controller;

import com.vti.crm.dto.request.OpportunityStatusRequest;
import com.vti.crm.dto.response.OpportunityStatusResponse;
import com.vti.crm.service.OpportunityStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunity-statuses")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OpportunityStatusController {

    private final OpportunityStatusService service;

    @GetMapping
    public List<OpportunityStatusResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public OpportunityStatusResponse getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public OpportunityStatusResponse create(@RequestBody OpportunityStatusRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public OpportunityStatusResponse update(
            @PathVariable Integer id,
            @RequestBody OpportunityStatusRequest request) {
        return service.update(id, request);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}