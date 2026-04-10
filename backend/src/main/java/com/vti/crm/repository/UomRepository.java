package com.vti.crm.repository;

import com.vti.crm.entity.Uom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UomRepository extends JpaRepository<Uom, Integer> {

    Optional<Uom> findByCode(String code);

    boolean existsByCode(String code);
}