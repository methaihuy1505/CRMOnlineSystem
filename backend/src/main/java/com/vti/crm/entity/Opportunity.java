package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "opportunities")
@Getter
@Setter
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "opportunity_code", unique = true)
    private String opportunityCode;

    private String name;

    @Column(name = "customer_id")
    private Integer customerId;

    @ManyToOne
    @JoinColumn(name = "stage_id")
    private OpportunityStage stage;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private OpportunityStatus status;

    @ManyToOne
    @JoinColumn(name = "lost_reason_id")
    private LostReason lostReason;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "remaining_amount")
    private Double remainingAmount;

    @Column(name = "probability")
    private Integer probability;
}