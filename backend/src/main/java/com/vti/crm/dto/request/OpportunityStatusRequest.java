package com.vti.crm.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityStatusRequest {
    private String code;
    private String name;
    private Boolean isFinal;
}