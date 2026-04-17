package com.vti.crm.controller;

import com.vti.crm.dto.request.OpportunityItemRequest;
import com.vti.crm.dto.response.OpportunityItemResponse;
import com.vti.crm.service.OpportunityItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunity-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OpportunityItemController {

    private final OpportunityItemService opportunityItemService;

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<OpportunityItemResponse>> getAll() {
        return ResponseEntity.ok(opportunityItemService.findAll());
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityItemResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(opportunityItemService.findById(id));
    }

    // ================= GET BY OPPORTUNITY ID =================
    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<List<OpportunityItemResponse>> getByOpportunityId(
            @PathVariable Integer opportunityId) {
        return ResponseEntity.ok(
                opportunityItemService.findByOpportunityId(opportunityId));
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<OpportunityItemResponse> create(
            @RequestBody OpportunityItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(opportunityItemService.create(request));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityItemResponse> update(
            @PathVariable Integer id,
            @RequestBody OpportunityItemRequest request) {
        return ResponseEntity.ok(opportunityItemService.update(id, request));
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        opportunityItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}