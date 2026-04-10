package com.vti.crm.repository;

import com.vti.crm.entity.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Integer> {
    List<CustomerFeedback> findByCustomerId(Integer customerId);
    List<CustomerFeedback> findByAssignedTo(Integer assignedTo);
}