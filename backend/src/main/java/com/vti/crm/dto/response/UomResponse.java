package com.vti.crm.dto.response;

import com.vti.crm.entity.Uom;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UomResponse {

    private Integer id;
    private String code;
    private String name;
    private Uom.Status status;
}