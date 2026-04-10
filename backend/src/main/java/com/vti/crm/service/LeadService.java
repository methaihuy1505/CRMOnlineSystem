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
    private final CommunicationDetailRepository communicationDetailRepository;

    // Inject thêm Repo để dùng getReferenceById
    private final SourceRepository sourceRepository;
    private final CampaignRepository campaignRepository;

    @Transactional
    public LeadResponse createLead(LeadCreateRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên Lead không được để trống!");
        }

        Lead savedLead = mapAndSaveLeadInfo(request);

        saveLeadInterests(savedLead, request.getProductInterestIds());
        saveCommunicationDetails(savedLead, request.getPhone(), request.getEmail());

        return mapToResponse(savedLead);
    }

    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public LeadResponse updateLead(Integer id, LeadUpdateRequest request) {
        Lead updatedLead = findAndMapUpdateLeadInfo(id, request);

        leadInterestRepository.deleteByLeadId(id);
        saveLeadInterests(updatedLead, request.getProductInterestIds());

        updateCommunicationDetails(updatedLead, request.getPhone(), request.getEmail());

        return mapToResponse(updatedLead);
    }

    private Lead mapAndSaveLeadInfo(LeadCreateRequest request) {
        Lead newLead = new Lead();
        // Map thông tin cơ bản
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

        // Map khóa ngoại qua Object (Chuẩn Join)
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
        existingLead.setExpectedRevenue(request.getExpectedRevenue());
        existingLead.setDescription(request.getDescription());

        // Cập nhật khóa ngoại qua Object
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

    private void saveCommunicationDetails(Lead lead, String phone, String email) {
        if (phone != null && !phone.trim().isEmpty()) {
            createNewCommDetail(lead, phone, CommunicationDetail.CommType.PHONE, "Số chính");
        }
        if (email != null && !email.trim().isEmpty()) {
            createNewCommDetail(lead, email, CommunicationDetail.CommType.EMAIL, "Email chính");
        }
    }

    private void updateCommunicationDetails(Lead lead, String newPhone, String newEmail) {
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            handleCommUpdate(lead, newPhone, CommunicationDetail.CommType.PHONE, "Số chính", "Số phụ");
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            handleCommUpdate(lead, newEmail, CommunicationDetail.CommType.EMAIL, "Email chính", "Email phụ");
        }
    }

    private void handleCommUpdate(Lead lead, String newValue, CommunicationDetail.CommType type, String primaryLabel, String secondaryLabel) {
        var currentPrimaryOpt = communicationDetailRepository.findByParentIdAndParentTypeAndCommTypeAndIsPrimaryTrue(
                lead.getId(), CommunicationDetail.ParentType.LEAD, type
        );

        if (currentPrimaryOpt.isPresent()) {
            CommunicationDetail currentPrimary = currentPrimaryOpt.get();
            if (!currentPrimary.getCommValue().equals(newValue)) {
                currentPrimary.setIsPrimary(false);
                currentPrimary.setLabel(secondaryLabel);
                communicationDetailRepository.save(currentPrimary);
                createNewCommDetail(lead, newValue, type, primaryLabel);
            }
        } else {
            createNewCommDetail(lead, newValue, type, primaryLabel);
        }
    }

    private void createNewCommDetail(Lead lead, String value, CommunicationDetail.CommType type, String label) {
        CommunicationDetail detail = new CommunicationDetail();
        detail.setParentId(lead.getId());
        detail.setParentType(CommunicationDetail.ParentType.LEAD);
        detail.setCommType(type);
        detail.setCommValue(value);
        detail.setIsPrimary(true);
        detail.setLabel(label);
        detail.setStatus(CommunicationDetail.Status.ACTIVE);
        communicationDetailRepository.save(detail);
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

                // Trả về cả ID và Tên cho FE (Lấy từ Object Join)
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