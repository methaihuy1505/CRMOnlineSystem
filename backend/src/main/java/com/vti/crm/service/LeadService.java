package com.vti.crm.service;

import com.vti.crm.dto.request.LeadCreateRequest;
import com.vti.crm.dto.request.LeadUpdateRequest;
import com.vti.crm.dto.response.LeadResponse;
import com.vti.crm.entity.*;
import com.vti.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadStatusRepository leadStatusRepository;
    private final LeadInterestRepository leadInterestRepository;
    private final SourceRepository sourceRepository;
    private final CampaignRepository campaignRepository;

    // Inject Shared Service
    private final CommunicationService communicationService;

    @Transactional
    public LeadResponse createLead(LeadCreateRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên Lead không được để trống!");
        }

        Lead savedLead = mapAndSaveLeadInfo(request);

        saveLeadInterests(savedLead, request.getProductInterestIds());

        // Gọi Shared Service thay vì viết lại logic
        communicationService.saveComms(
                savedLead.getId(),
                CommunicationDetail.ParentType.LEAD,
                request.getPhone(),
                request.getEmail(),
                null // Lead không có fax
        );

        return mapToResponse(savedLead);
    }

    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LeadResponse getLeadById(Integer id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng tiềm năng với ID: " + id));
        return mapToResponse(lead);
    }

    @Transactional
    public LeadResponse updateLead(Integer id, LeadUpdateRequest request) {
        Lead updatedLead = findAndMapUpdateLeadInfo(id, request);

        leadInterestRepository.deleteByLeadId(id);
        saveLeadInterests(updatedLead, request.getProductInterestIds());

        // Gọi Shared Service thay vì viết lại logic
        communicationService.updateComms(
                updatedLead.getId(),
                CommunicationDetail.ParentType.LEAD,
                request.getPhone(),
                request.getEmail(),
                null // Lead không có fax
        );

        return mapToResponse(updatedLead);
    }

    @Transactional
    public void deleteLead(Integer id) {
        Lead existingLead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lead với ID: " + id));

        existingLead.setDeletedAt(java.time.LocalDateTime.now());
        leadRepository.save(existingLead);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Lead mapAndSaveLeadInfo(LeadCreateRequest request) {
        Lead newLead = new Lead();
        newLead.setFullName(request.getFullName());
        newLead.setCompanyName(request.getCompanyName());
        newLead.setPhone(request.getPhone());
        newLead.setEmail(request.getEmail());
        newLead.setWebsite(request.getWebsite());
        newLead.setTaxCode(request.getTaxCode());
        newLead.setCitizenId(request.getCitizenId());
        newLead.setAddress(request.getAddress());
        newLead.setExpectedRevenue(request.getExpectedRevenue());
        newLead.setDescription(request.getDescription());

        if (request.getSourceId() != null) {
            newLead.setSource(sourceRepository.getReferenceById(request.getSourceId()));
        }
        if (request.getCampaignId() != null) {
            newLead.setCampaign(campaignRepository.getReferenceById(request.getCampaignId()));
        }
        if (request.getStatusId() != null) {
            newLead.setStatus(leadStatusRepository.getReferenceById(request.getStatusId()));
        }

        newLead.setProvinceId(request.getProvinceId());
        newLead.setBranchId(request.getBranchId());
        newLead.setAssignedTo(request.getAssignedTo());

        return leadRepository.save(newLead);
    }

    private Lead findAndMapUpdateLeadInfo(Integer id, LeadUpdateRequest request) {
        Lead existingLead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lead với ID: " + id));

        existingLead.setFullName(request.getFullName());
        existingLead.setCompanyName(request.getCompanyName());
        existingLead.setPhone(request.getPhone());
        existingLead.setEmail(request.getEmail());
        existingLead.setWebsite(request.getWebsite());
        existingLead.setAddress(request.getAddress());
        existingLead.setTaxCode(request.getTaxCode());
        existingLead.setCitizenId(request.getCitizenId());
        existingLead.setExpectedRevenue(request.getExpectedRevenue());
        existingLead.setDescription(request.getDescription());

        if (request.getSourceId() != null) {
            existingLead.setSource(sourceRepository.getReferenceById(request.getSourceId()));
        } else {
            existingLead.setSource(null);
        }

        if (request.getCampaignId() != null) {
            existingLead.setCampaign(campaignRepository.getReferenceById(request.getCampaignId()));
        } else {
            existingLead.setCampaign(null);
        }

        if (request.getStatusId() != null) {
            existingLead.setStatus(leadStatusRepository.getReferenceById(request.getStatusId()));
        }

        existingLead.setProvinceId(request.getProvinceId());
        existingLead.setBranchId(request.getBranchId());
        existingLead.setAssignedTo(request.getAssignedTo());

        return leadRepository.save(existingLead);
    }

    private void saveLeadInterests(Lead lead, List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        List<LeadInterest> interests = productIds.stream().map(productId -> {
            LeadInterest interest = new LeadInterest();
            interest.setLead(lead);
            interest.setProductId(productId);
            return interest;
        }).toList();
        leadInterestRepository.saveAll(interests);
    }

    private LeadResponse mapToResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .fullName(lead.getFullName())
                .companyName(lead.getCompanyName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .website(lead.getWebsite())
                .taxCode(lead.getTaxCode())
                .citizenId(lead.getCitizenId())
                .address(lead.getAddress())
                .expectedRevenue(lead.getExpectedRevenue())
                .description(lead.getDescription())
                .totalCalls(lead.getTotalCalls())
                .totalEmails(lead.getTotalEmails())
                .totalMeetings(lead.getTotalMeetings())
                .sourceId(lead.getSource() != null ? lead.getSource().getId() : null)
                .sourceName(lead.getSource() != null ? lead.getSource().getName() : "Tự nhiên")
                .campaignId(lead.getCampaign() != null ? lead.getCampaign().getId() : null)
                .campaignName(lead.getCampaign() != null ? lead.getCampaign().getName() : null)
                .statusId(lead.getStatus() != null ? lead.getStatus().getId() : null)
                .statusName(lead.getStatus() != null ? lead.getStatus().getName() : null)
                .provinceId(lead.getProvinceId())
                .branchId(lead.getBranchId())
                .assignedTo(lead.getAssignedTo())
                .createdBy(lead.getCreatedBy())
                .updatedBy(lead.getUpdatedBy())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}