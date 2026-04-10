package com.vti.crm.entity;


import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "lost_reasons")
@Getter
@Setter
public class LostReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}