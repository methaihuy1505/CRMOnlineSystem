package com.vti.crm.repository;

import com.vti.crm.entity.OpportunityStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityStageRepository
        extends JpaRepository<OpportunityStage, Integer> {
}