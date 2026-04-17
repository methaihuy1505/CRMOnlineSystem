package com.vti.crm.controller;


import com.vti.crm.dto.request.OpportunityStageRequest;
import com.vti.crm.dto.response.OpportunityStageResponse;
import com.vti.crm.service.OpportunityStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opportunity-stages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OpportunityStageController {

    private final OpportunityStageService service;

    @PostMapping
    public ResponseEntity<OpportunityStageResponse> create(
            @RequestBody OpportunityStageRequest request) {

        OpportunityStageResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityStageResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityStageResponse> update(
            @PathVariable Integer id,
            @RequestBody OpportunityStageRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}