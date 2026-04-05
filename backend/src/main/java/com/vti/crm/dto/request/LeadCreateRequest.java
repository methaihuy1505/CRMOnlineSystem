package com.vti.crm.dto.request;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeadCreateRequest {

    // --- Thông tin cơ bản ---
    private String fullName;
    private String companyName;
    private String phone;
    private String email;
    private String website;
    private String taxCode;
    private String citizenId;
    private String address;
    private BigDecimal expectedRevenue;
    private String description;

    // --- Các khóa ngoại (ID) ---
    private Integer provinceId;
    private Integer branchId;
    private Integer sourceId;
    private Integer campaignId;
    private Integer statusId;
    private Integer assignedTo;

    // --- Danh sách Sản phẩm quan tâm ---
    private List<Integer> productInterestIds;
}