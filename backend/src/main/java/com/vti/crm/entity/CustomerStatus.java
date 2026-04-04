package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_status")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;
}