package com.vti.crm.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LeadResponse {
    private Integer id;
    private String fullName;
    private String companyName;
    private String phone;
    private String email;
    private String statusName; // Trả về tên trạng thái (Mới, Đang liên hệ...) thay vì ID
    private BigDecimal expectedRevenue;
    private LocalDateTime createdAt;
}