package com.vti.crm.controller;

import com.vti.crm.dto.request.OpportunityRequest;
import com.vti.crm.dto.response.OpportunityResponse;
import com.vti.crm.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OpportunityController {

    private final OpportunityService opportunityService;

    // ======================= GET ALL =======================
    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAll() {
        List<OpportunityResponse> opportunities = opportunityService.findAll();
        return ResponseEntity.ok(opportunities);
    }

    // ======================= GET BY ID =======================
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getById(@PathVariable Integer id) {
        OpportunityResponse opportunity = opportunityService.findById(id);
        return ResponseEntity.ok(opportunity);
    }

    // ======================= CREATE =======================
    @PostMapping
    public ResponseEntity<OpportunityResponse> create(
            @Valid @RequestBody OpportunityRequest request) {

        OpportunityResponse response = opportunityService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ======================= UPDATE =======================
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody OpportunityRequest request) {

        OpportunityResponse response = opportunityService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ======================= DELETE =======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/bulk")
    public List<OpportunityResponse> createBulk(
            @RequestBody List<OpportunityRequest> requests) {
        return opportunityService.createBulk(requests);
    }
//    @GetMapping("/stats/active-leads")
//    public ResponseEntity<Long> getActiveLeads() {
//        long count = opportunityService.getActiveLeadsCount();
//        return ResponseEntity.ok(count);
//    }

}