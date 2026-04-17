package com.vti.crm.dto.response;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityDashboardStatsResponse {
    // Total Pipeline
    private String totalPipeline;   // VD: "$4.2M"
    private String pipelineTrend;   // VD: "+12%"
    private String pipelineTrendIcon; // "trending_up" hoặc "trending_down"

    // Active Leads
    private String activeLeads;     // VD: "148"

    // Avg Deal Size
    private String avgDealSize;     // VD: "$28.4k"

    // Conversion Rate
    private String conversionRate;  // VD: "24.8%"
}