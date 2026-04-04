package com.vti.crm.repository;

import com.vti.crm.entity.CommunicationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationDetailRepository extends JpaRepository<CommunicationDetail, Integer> {
    
    // Tìm danh sách liên lạc dựa theo Loại đối tượng (Lead/Customer) và ID của đối tượng đó
    List<CommunicationDetail> findByParentTypeAndParentId(CommunicationDetail.ParentType parentType, Integer parentId);
}