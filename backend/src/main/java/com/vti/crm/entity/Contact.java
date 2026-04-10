package com.vti.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    private LocalDate birthday;

    @Column(name = "personal_email", length = 100)
    private String personalEmail;

    @Column(name = "personal_phone", length = 20)
    private String personalPhone;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}
