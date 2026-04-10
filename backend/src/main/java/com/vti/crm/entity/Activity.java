package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    public enum ParentType {
        LEAD, CUSTOMER, OPPORTUNITY
    }

    public enum ActivityType {
        CALL, MEETING, NOTE, EMAIL_QUOTE, EMAIL_TRANSACTION
    }

    public enum Status {
        PLANNED, HELD, NOT_HELD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_type", nullable = false, length = 20)
    private ParentType parentType;

    @Column(name = "contact_id")
    private Integer contactId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    private ActivityType activityType;

    @Column(length = 255)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    @Column(name = "activity_date")
    private LocalDateTime activityDate;

    @Column(name = "next_follow_up_date")
    private LocalDateTime nextFollowUpDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Column(name = "assigned_to")
    private Integer assignedTo;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
