package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_ranks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;
}