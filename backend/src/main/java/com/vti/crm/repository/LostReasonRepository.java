package com.vti.crm.repository;

import com.vti.crm.entity.LostReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LostReasonRepository
        extends JpaRepository<LostReason, Integer> {

    Optional<LostReason> findByCode(String code);
    boolean existsByCode(String code);
}