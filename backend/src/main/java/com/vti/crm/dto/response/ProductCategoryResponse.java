package com.vti.crm.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryResponse {
    private Integer id;
    private String name;
    private String description;
}