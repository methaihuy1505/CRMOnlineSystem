package com.vti.crm.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class LeadUpdateRequest {
    private String fullName;
    private String companyName;
    private String phone;
    private String email;
    private String website;
    private String address;

    // Các trường khóa ngoại cần cập nhật
    private Integer provinceId;
    private Integer branchId;
    private Integer sourceId;
    private Integer campaignId;
    private Integer statusId;
    private Integer assignedTo;

    private BigDecimal expectedRevenue;
    private String description;
    private List<Integer> productInterestIds;
}