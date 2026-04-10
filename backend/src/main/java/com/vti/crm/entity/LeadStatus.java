package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lead_status")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LeadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;
}