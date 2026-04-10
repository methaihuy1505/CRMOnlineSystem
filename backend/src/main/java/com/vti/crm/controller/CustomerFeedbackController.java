package com.vti.crm.controller;

import com.vti.crm.dto.request.CustomerFeedbackRequest;
import com.vti.crm.dto.response.CustomerFeedbackResponse;
import com.vti.crm.service.CustomerFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer-feedbacks")
@RequiredArgsConstructor
public class CustomerFeedbackController {

    private final CustomerFeedbackService customerFeedbackService;

    @GetMapping
    public ResponseEntity<List<CustomerFeedbackResponse>> getAllFeedbacks() {
        return ResponseEntity.ok(customerFeedbackService.getAllFeedbacks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerFeedbackResponse> getFeedbackById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerFeedbackService.getFeedbackById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerFeedbackResponse> createFeedback(@RequestBody CustomerFeedbackRequest request) {
        return new ResponseEntity<>(customerFeedbackService.createFeedback(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerFeedbackResponse> updateFeedback(
            @PathVariable Integer id,
            @RequestBody CustomerFeedbackRequest request) {
        return ResponseEntity.ok(customerFeedbackService.updateFeedback(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        customerFeedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerFeedbackResponse>> getFeedbacksByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customerFeedbackService.getFeedbacksByCustomerId(customerId));
    }
}