package com.vti.crm.service;

import com.vti.crm.dto.request.CampaignRequest;
import com.vti.crm.dto.response.CampaignResponse;
import com.vti.crm.entity.Campaign;
import com.vti.crm.entity.Customer;
import com.vti.crm.entity.Lead;
import com.vti.crm.repository.CampaignRepository;
import com.vti.crm.repository.CustomerRepository;
import com.vti.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;

    public List<CampaignResponse> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CampaignResponse getCampaignById(Integer id) {
        // 1. Tìm chiến dịch hoặc báo lỗi nếu không tồn tại
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));

        // 2. Lấy danh sách Leads và Customers liên quan từ Repository
        List<Lead> campaignLeads = leadRepository.findByCampaignId(id);
        List<Customer> campaignCustomers = customerRepository.findByCampaignId(id);

        // 3. Tính toán Dự thu (Expected Revenue) - Chỉ tính cho Lead Status 1 (Mới) và 2 (Đang liên hệ)
        // Sử dụng BigDecimal để tránh lỗi "Required type: double, Provided: BigDecimal"
        BigDecimal expected = campaignLeads.stream()
                .filter(l -> l.getStatus() != null && (l.getStatus().getId() == 1 || l.getStatus().getId() == 2))
                .map(l -> l.getExpectedRevenue() != null ? l.getExpectedRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tính toán Thực thu (Actual Revenue) - Tính cho Lead Status 3 (Đã chuyển đổi) và 4 (Phát sinh giao dịch)
        BigDecimal actual = campaignLeads.stream()
                .filter(l -> l.getStatus() != null && (l.getStatus().getId() == 3 || l.getStatus().getId() == 4))
                .map(l -> l.getExpectedRevenue() != null ? l.getExpectedRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Trả về DTO hoàn chỉnh kèm theo các chỉ số thống kê cho Frontend
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .description(campaign.getDescription()) // Đã bổ sung trường mô tả mới
                .totalLeads((long) campaignLeads.size())
                .totalCustomers((long) campaignCustomers.size())
                .expectedRevenue(expected.doubleValue()) // Convert về double để phù hợp với DTO Response
                .actualRevenue(actual.doubleValue())     // Convert về double để phù hợp với DTO Response
                .build();
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        Campaign campaign = new Campaign();
        campaign.setName(request.getName());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        Campaign savedCampaign = campaignRepository.save(campaign);
        return mapToResponse(savedCampaign);
    }

    @Transactional
    public CampaignResponse updateCampaign(Integer id, CampaignRequest request) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));

        campaign.setName(request.getName());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToResponse(updatedCampaign);
    }

    @Transactional
    public void deleteCampaign(Integer id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));

        // Thực hiện Xóa mềm (Soft Delete)
        campaign.setDeletedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .build();
    }
}