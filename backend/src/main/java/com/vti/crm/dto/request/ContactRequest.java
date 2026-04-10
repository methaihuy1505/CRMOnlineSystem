package com.vti.crm.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContactRequest {
    private Integer customerId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private LocalDate birthday;
    private String personalEmail;
    private String personalPhone;
    private Boolean isPrimary;
}
