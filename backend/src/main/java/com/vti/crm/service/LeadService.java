package com.vti.crm.service;

import com.vti.crm.dto.request.LeadCreateRequest;
import com.vti.crm.dto.request.LeadUpdateRequest;
import com.vti.crm.dto.response.LeadResponse;
import com.vti.crm.entity.CommunicationDetail;
import com.vti.crm.entity.Lead;
import com.vti.crm.entity.LeadInterest;
import com.vti.crm.entity.LeadStatus;
import com.vti.crm.repository.CommunicationDetailRepository;
import com.vti.crm.repository.LeadInterestRepository;
import com.vti.crm.repository.LeadRepository;
import com.vti.crm.repository.LeadStatusRepository;
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

    // ==========================================
    // 1. CÁC HÀM CHÍNH (PUBLIC API)
    // ==========================================

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
        // 1. Cập nhật thông tin cơ bản của Lead
        Lead updatedLead = findAndMapUpdateLeadInfo(id, request);

        // 2. Cập nhật Products (Xóa sạch cái cũ, tạo lại cái mới)
        leadInterestRepository.deleteByLeadId(id);
        saveLeadInterests(updatedLead, request.getProductInterestIds());

        // 3. Cập nhật Liên lạc (Nghiệp vụ: Giáng cấp cũ thành phụ, lưu mới thành chính)
        updateCommunicationDetails(updatedLead, request.getPhone(), request.getEmail());

        return mapToResponse(updatedLead);
    }

    // ==========================================
    // 2. CÁC HÀM MAP DỮ LIỆU LEAD (PRIVATE)
    // ==========================================

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

        newLead.setProvinceId(request.getProvinceId());
        newLead.setBranchId(request.getBranchId());
        newLead.setSourceId(request.getSourceId());
        newLead.setCampaignId(request.getCampaignId());
        newLead.setAssignedTo(request.getAssignedTo());

        if (request.getStatusId() != null) {
            LeadStatus status = leadStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái Lead có ID: " + request.getStatusId()));
            newLead.setStatus(status);
        }

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

        existingLead.setProvinceId(request.getProvinceId());
        existingLead.setBranchId(request.getBranchId());
        existingLead.setSourceId(request.getSourceId());
        existingLead.setCampaignId(request.getCampaignId());
        existingLead.setAssignedTo(request.getAssignedTo());

        if (request.getStatusId() != null) {
            LeadStatus status = leadStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Trạng thái không hợp lệ"));
            existingLead.setStatus(status);
        }

        return leadRepository.save(existingLead);
    }

    // ==========================================
    // 3. CÁC HÀM XỬ LÝ NGHIỆP VỤ PHỤ (PRIVATE)
    // ==========================================

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

    // Dùng cho hàm Create
    private void saveCommunicationDetails(Lead lead, String phone, String email) {
        if (phone != null && !phone.trim().isEmpty()) {
            createNewCommDetail(lead, phone, CommunicationDetail.CommType.PHONE, "Số chính");
        }
        if (email != null && !email.trim().isEmpty()) {
            createNewCommDetail(lead, email, CommunicationDetail.CommType.EMAIL, "Email chính");
        }
    }

    // Dùng cho hàm Update
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
            // Chỉ cập nhật nếu user nhập số mới khác số cũ
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
                .statusName(lead.getStatus() != null ? lead.getStatus().getName() : null)
                .expectedRevenue(lead.getExpectedRevenue())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}