package com.vti.crm.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "opportunity_items")
@Getter
@Setter
public class Opportunity_items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "opportunity_id")
    private Opportunities opportunity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    // SNAPSHOT (đúng design CRM)
    @Column(name = "product_name")
    private String productName;

    @Column(name = "uom_name")
    private String uomName;

    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "vat_rate")
    private Double vatRate;

    @Column(name = "vat_amount")
    private Double vatAmount;

    @Column(name = "discount_rate")
    private Double discountRate;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "final_line_total")
    private Double finalLineTotal;
}