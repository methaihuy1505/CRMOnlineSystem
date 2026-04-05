package com.vti.crm.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "opportunity_status")
@Getter
@Setter
public class Opportunity_status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String name;

    @Column(name = "is_final")
    private Boolean isFinal;
}