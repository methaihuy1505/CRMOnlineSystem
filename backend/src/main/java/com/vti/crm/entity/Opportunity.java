package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "opportunity_code", unique = true)
    private String opportunityCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "customer_id", nullable = false)
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

    private Integer probability;

    private String description;

    // --- CÁC TRƯỜNG THỜI GIAN CẦN THIẾT ---

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp // Tự động lấy record hiện tại khi tạo mới
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp // Tự động cập nhật khi update record
    private LocalDateTime updatedAt;

    // ---------------------------------------

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityItem> items = new ArrayList<>();
}