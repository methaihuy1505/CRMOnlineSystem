package com.vti.crm.service;

import com.vti.crm.dto.request.OpportunityRequest;
import com.vti.crm.dto.response.OpportunityResponse;
import com.vti.crm.dto.response.OpportunityDashboardStatsResponse;
import com.vti.crm.entity.*;
import com.vti.crm.exception.ResourceNotFoundException;
import com.vti.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityStageRepository stageRepository;
    private final OpportunityStatusRepository statusRepository;
    private final LostReasonRepository lostReasonRepository;

    // ======================= GET ALL =======================
    @Transactional(readOnly = true)
    public List<OpportunityResponse> findAll() {
        return opportunityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ======================= GET BY ID =======================
    @Transactional(readOnly = true)
    public OpportunityResponse findById(Integer id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Opportunity not found with id: " + id));

        return mapToResponse(opportunity);
    }

    // ======================= CREATE =======================
    public OpportunityResponse create(OpportunityRequest request) {
        if (opportunityRepository.existsByOpportunityCode(request.getOpportunityCode())) {
            throw new IllegalArgumentException("Opportunity code already exists!");
        }

        Opportunity opportunity = new Opportunity();

        // Gán mặc định là 0 trước khi map
        opportunity.setTotalAmount(0.0);
        opportunity.setDepositAmount(0.0);
        opportunity.setRemainingAmount(0.0);

        mapToEntity(request, opportunity);

        // Lưu ý: Nếu khi create chưa có item thì total vẫn là 0
        // Nếu create có kèm item thì gọi hàm tính toán ở đây
        recalculateOpportunityTotal(opportunity);

        return mapToResponse(opportunityRepository.save(opportunity));
    }
    public void recalculateOpportunityTotal(Opportunity opportunity) {
        if (opportunity.getItems() == null || opportunity.getItems().isEmpty()) {
            opportunity.setTotalAmount(0.0);
        } else {
            // Tính tổng finalLineTotal từ tất cả items
            double total = opportunity.getItems().stream()
                    .map(item -> item.getFinalLineTotal())
                    .filter(val -> val != null)
                    .mapToDouble(BigDecimal::doubleValue) // Chuyển từ BigDecimal sang double
                    .sum();

            opportunity.setTotalAmount(total);
        }
    }
    private void calculateRemainingAmount(Opportunity opportunity) {
        double total = (opportunity.getTotalAmount() != null) ? opportunity.getTotalAmount() : 0.0;
        double deposit = (opportunity.getDepositAmount() != null) ? opportunity.getDepositAmount() : 0.0;

        opportunity.setRemainingAmount(total - deposit);
    }
    // ======================= UPDATE =======================
    public OpportunityResponse update(Integer id, OpportunityRequest request) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Opportunity not found with id: " + id));

        mapToEntity(request, opportunity);
        calculateAmounts(opportunity);

        return mapToResponse(opportunityRepository.save(opportunity));
    }

    // ======================= DELETE =======================
    public void delete(Integer id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Opportunity not found with id: " + id));

        opportunityRepository.delete(opportunity);
    }

    // ======================= MAPPING: REQUEST → ENTITY =======================
    private void mapToEntity(OpportunityRequest request, Opportunity opportunity) {
        opportunity.setOpportunityCode(request.getOpportunityCode());
        opportunity.setName(request.getName());
        opportunity.setCustomerId(request.getCustomerId());
        opportunity.setProbability(request.getProbability());

        // Deposit amount lấy từ request, nếu null thì để 0.0
        opportunity.setDepositAmount(request.getDepositAmount() != null ? request.getDepositAmount() : 0.0);

        // Không set TotalAmount từ request nữa vì ta sẽ tính từ Items
        // opportunity.setTotalAmount(request.getTotalAmount()); -> BỎ DÒNG NÀY

        if (request.getStageId() != null) {
            OpportunityStage stage = stageRepository.findById(request.getStageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stage not found"));
            opportunity.setStage(stage);
        }

        if (request.getStatusId() != null) {
            OpportunityStatus status = statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            opportunity.setStatus(status);
        }

        if (request.getLostReasonId() != null) {
            LostReason lostReason = lostReasonRepository.findById(request.getLostReasonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lost reason not found"));
            opportunity.setLostReason(lostReason);
        }
    }

    // ======================= MAPPING: ENTITY → RESPONSE =======================
    private OpportunityResponse mapToResponse(Opportunity opportunity) {
        return OpportunityResponse.builder()
                .id(opportunity.getId())
                .opportunityCode(opportunity.getOpportunityCode())
                .name(opportunity.getName())
                .customerId(opportunity.getCustomerId())
                .stageId(opportunity.getStage() != null ? opportunity.getStage().getId() : null)
                .stageName(opportunity.getStage() != null ? opportunity.getStage().getName() : null)
                .statusId(opportunity.getStatus() != null ? opportunity.getStatus().getId() : null)
                .statusName(opportunity.getStatus() != null ? opportunity.getStatus().getName() : null)
                .lostReasonId(opportunity.getLostReason() != null ? opportunity.getLostReason().getId() : null)
                .lostReasonName(opportunity.getLostReason() != null ? opportunity.getLostReason().getName() : null)
                .totalAmount(opportunity.getTotalAmount())
                .depositAmount(opportunity.getDepositAmount())
                .remainingAmount(opportunity.getRemainingAmount())
                .probability(opportunity.getProbability())
                .build();
    }

    // ======================= BUSINESS LOGIC =======================
    private void calculateAmounts(Opportunity opportunity) {
        if (opportunity.getTotalAmount() != null &&
                opportunity.getDepositAmount() != null) {

            opportunity.setRemainingAmount(
                    opportunity.getTotalAmount() - opportunity.getDepositAmount()
            );
        }
    }


    // ======================= TOTAL PIPELINE =======================

    public OpportunityDashboardStatsResponse getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfThisMonth.minusNanos(1);

        // --- 1. Xử lý Total Pipeline ---
        Double totalPipelineRaw = opportunityRepository.sumAllTotalAmount();
        totalPipelineRaw = (totalPipelineRaw != null) ? totalPipelineRaw : 0.0;

        Double thisMonthAmount = opportunityRepository.sumTotalAmountByDateRange(startOfThisMonth, now);
        thisMonthAmount = (thisMonthAmount != null) ? thisMonthAmount : 0.0;

        Double lastMonthAmount = opportunityRepository.sumTotalAmountByDateRange(startOfLastMonth, endOfLastMonth);
        lastMonthAmount = (lastMonthAmount != null) ? lastMonthAmount : 0.0;

        double growthRate = (lastMonthAmount > 0) ? ((thisMonthAmount - lastMonthAmount) / lastMonthAmount) * 100 : (thisMonthAmount > 0 ? 100.0 : 0.0);
        String trendIcon = growthRate >= 0 ? "trending_up" : "trending_down";
        String trendText = String.format("%s%.1f%% vs last month", growthRate >= 0 ? "+" : "", growthRate);

        // --- 2. Xử lý Active Leads ---
        // Giả sử 1,2,3 là ID của New, Contacted, Qualifying
        long activeLeadsCount = opportunityRepository.countByStatusIdIn(List.of(1, 2, 3));

        // --- 3. Xử lý Avg Deal Size ---
        Object[] avgStats = (Object[]) opportunityRepository.getSumAndCountByDateRange(startOfThisMonth, now)[0];
        Double sumVal = (avgStats[0] != null) ? (Double) avgStats[0] : 0.0;
        Long countVal = (avgStats[1] != null) ? (Long) avgStats[1] : 0L;
        double avgDealSize = (countVal > 0) ? (sumVal / countVal) : 0.0;

        // --- 4. Xử lý Conversion Rate ---
        List<Object[]> conversionData = opportunityRepository.countClosedOpportunities();
        long won = 0, lost = 0;
        for (Object[] row : conversionData) {
            if ("Won".equalsIgnoreCase((String) row[0])) won = (long) row[1];
            if ("Lost".equalsIgnoreCase((String) row[0])) lost = (long) row[1];
        }
        double convRate = (won + lost > 0) ? ((double) won / (won + lost)) * 100 : 0.0;

        // --- TRẢ VỀ DỮ LIỆU ĐÃ FORMAT ---
        return OpportunityDashboardStatsResponse.builder()
                .totalPipeline(formatToCurrency(totalPipelineRaw)) // Ví dụ: $4.2M
                .pipelineTrend(trendText)
                .pipelineTrendIcon(trendIcon)
                .activeLeads(String.valueOf(activeLeadsCount))
                .avgDealSize(formatToK(avgDealSize))
                .conversionRate(String.format("%.1f%%", convRate))
                .build();
    }

    // Hàm format tiền triệu (M)
    private String formatToCurrency(double value) {
        if (value >= 1000000) return String.format("$%.1fM", value / 1000000);
        if (value >= 1000) return String.format("$%.1fk", value / 1000);
        return String.format("$%.0f", value);
    }

    // Hàm format tiền nghìn (k)
    private String formatToK(double value) {
        if (value >= 1000) return String.format("$%.1fk", value / 1000);
        return String.format("$%.1f", value);
    }
    @Transactional
    public List<OpportunityResponse> createBulk(List<OpportunityRequest> requests) {
        return requests.stream()
                .map(this::create)
                .toList();
    }
}