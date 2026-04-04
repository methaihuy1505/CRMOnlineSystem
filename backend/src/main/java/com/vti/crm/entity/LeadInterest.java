package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lead_interests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LeadInterest {

    // Bảng này là khóa chính kép (lead_id, product_id) trong DB,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Trỏ thẳng về Lead
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Column(name = "product_id", nullable = false)
    private Integer productId;
}