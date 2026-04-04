package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "contacts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Khóa ngoại trỏ thẳng đến Customer Entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "personal_email", length = 100)
    private String personalEmail;

    @Column(name = "personal_phone", length = 20)
    private String personalPhone;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;
}