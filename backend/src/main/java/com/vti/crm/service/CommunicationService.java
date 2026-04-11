package com.vti.crm.service;

import com.vti.crm.entity.CommunicationDetail;
import com.vti.crm.repository.CommunicationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final CommunicationDetailRepository commRepository;

    // Dùng cho chức năng Create (Tạo mới)
    public void saveComms(Integer parentId, CommunicationDetail.ParentType parentType, String phone, String email, String fax) {
        if (phone != null && !phone.trim().isEmpty()) {
            createNewCommDetail(parentId, parentType, phone, CommunicationDetail.CommType.PHONE, "Số chính");
        }
        if (email != null && !email.trim().isEmpty()) {
            createNewCommDetail(parentId, parentType, email, CommunicationDetail.CommType.EMAIL, "Email chính");
        }
        if (fax != null && !fax.trim().isEmpty()) {
            createNewCommDetail(parentId, parentType, fax, CommunicationDetail.CommType.FAX, "Fax");
        }
    }

    // Dùng cho chức năng Update (Cập nhật và lưu vết lịch sử)
    public void updateComms(Integer parentId, CommunicationDetail.ParentType parentType, String newPhone, String newEmail, String newFax) {
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            handleCommUpdate(parentId, parentType, newPhone, CommunicationDetail.CommType.PHONE, "Số chính", "Số phụ");
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            handleCommUpdate(parentId, parentType, newEmail, CommunicationDetail.CommType.EMAIL, "Email chính", "Email phụ");
        }
        if (newFax != null && !newFax.trim().isEmpty()) {
            handleCommUpdate(parentId, parentType, newFax, CommunicationDetail.CommType.FAX, "Fax", "Fax cũ");
        }
    }

    // --- Private Helper Methods ---

    private void handleCommUpdate(Integer parentId, CommunicationDetail.ParentType parentType, String newValue, CommunicationDetail.CommType type, String primaryLabel, String secondaryLabel) {
        var currentPrimaryOpt = commRepository.findByParentIdAndParentTypeAndCommTypeAndIsPrimaryTrue(
                parentId, parentType, type
        );

        if (currentPrimaryOpt.isPresent()) {
            CommunicationDetail currentPrimary = currentPrimaryOpt.get();
            if (!currentPrimary.getCommValue().equals(newValue)) {
                // Đổi số cũ thành số phụ
                currentPrimary.setIsPrimary(false);
                currentPrimary.setLabel(secondaryLabel);
                commRepository.save(currentPrimary);
                // Tạo số mới làm số chính
                createNewCommDetail(parentId, parentType, newValue, type, primaryLabel);
            }
        } else {
            createNewCommDetail(parentId, parentType, newValue, type, primaryLabel);
        }
    }

    private void createNewCommDetail(Integer parentId, CommunicationDetail.ParentType parentType, String value, CommunicationDetail.CommType type, String label) {
        CommunicationDetail detail = new CommunicationDetail();
        detail.setParentId(parentId);
        detail.setParentType(parentType);
        detail.setCommType(type);
        detail.setCommValue(value);
        detail.setIsPrimary(true);
        detail.setLabel(label);
        detail.setStatus(CommunicationDetail.Status.ACTIVE);
        commRepository.save(detail);
    }
}