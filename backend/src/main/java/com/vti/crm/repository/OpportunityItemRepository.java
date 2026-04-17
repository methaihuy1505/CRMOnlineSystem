package com.vti.crm.repository;

import com.vti.crm.entity.OpportunityItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityItemRepository extends JpaRepository<OpportunityItem, Integer> {

    List<OpportunityItem> findByOpportunityIdOrderByLineItemNumberAsc(Integer opportunityId);

    void deleteByOpportunityId(Integer opportunityId);
}