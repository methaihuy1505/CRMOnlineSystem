package com.vti.crm.repository;

import com.vti.crm.entity.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpportunityStatusRepository
        extends JpaRepository<OpportunityStatus, Integer> {

    Optional<OpportunityStatus> findByCode(String code);
    boolean existsByCode(String code);
}