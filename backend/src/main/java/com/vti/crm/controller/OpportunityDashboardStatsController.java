package com.vti.crm.controller;

import com.vti.crm.dto.response.OpportunityDashboardStatsResponse;
import com.vti.crm.service.OpportunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class OpportunityDashboardStatsController {

    private final OpportunityService opportunityService;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<OpportunityDashboardStatsResponse> getStats() {
        return ResponseEntity.ok(opportunityService.getDashboardStats());
    }
}