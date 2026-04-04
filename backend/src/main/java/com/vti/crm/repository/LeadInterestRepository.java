package com.vti.crm.repository;

import com.vti.crm.entity.LeadInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadInterestRepository extends JpaRepository<LeadInterest, Integer> {
}
