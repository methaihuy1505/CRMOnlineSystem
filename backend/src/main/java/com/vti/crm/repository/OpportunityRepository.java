package com.vti.crm.repository;

import com.vti.crm.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Integer> {

    Optional<Opportunity> findByOpportunityCode(String opportunityCode);

    boolean existsByOpportunityCode(String opportunityCode);
    // Tính tổng total_amount của các cơ hội được tạo trong một khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM Opportunity o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    Double sumTotalAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Tính tổng toàn bộ Pipeline hiện có (không quan trọng thời gian)
    @Query("SELECT SUM(o.totalAmount) FROM Opportunity o")
    Double sumAllTotalAmount();

    // Đếm số lượng các bản ghi có status_id nằm trong danh sách "Active"
    long countByStatusIdIn(List<Integer> statusIds);

    // Hoặc nếu bạn muốn đếm theo một status_id cụ thể
    long countByStatusId(Integer statusId);

    // Trả về một Object chứa [Tổng tiền, Tổng số bản ghi]
    @Query("SELECT SUM(o.totalAmount), COUNT(o) FROM Opportunity o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    Object[] getSumAndCountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Đếm số lượng theo ID trạng thái

    // Cách linh hoạt hơn: Đếm theo tên trạng thái (ví dụ: 'Won', 'Lost')
    @Query("SELECT o.status.name, COUNT(o) FROM Opportunity o " +
            "WHERE o.status.name IN ('Won', 'Lost') " +
            "GROUP BY o.status.name")
    List<Object[]> countClosedOpportunities();
}