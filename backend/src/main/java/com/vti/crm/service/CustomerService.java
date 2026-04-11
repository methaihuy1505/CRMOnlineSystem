package com.vti.crm.service;

import com.vti.crm.dto.request.CustomerCreateRequest;
import com.vti.crm.dto.request.CustomerUpdateRequest;
import com.vti.crm.dto.response.CustomerResponse;
import com.vti.crm.entity.CommunicationDetail;
import com.vti.crm.entity.Contact;
import com.vti.crm.entity.Customer;
import com.vti.crm.repository.ContactRepository;
import com.vti.crm.repository.CustomerRankRepository;
import com.vti.crm.repository.CustomerRepository;
import com.vti.crm.repository.CustomerStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerStatusRepository customerStatusRepository;
    private final CustomerRankRepository customerRankRepository;
    private final ContactRepository contactRepository;

    // Inject Shared Service
    private final CommunicationService communicationService;

    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setCustomerCode("CUS-" + System.currentTimeMillis());

        mapRequestToEntity(request, customer);
        Customer savedCustomer = customerRepository.save(customer);

        // Lưu thông tin liên lạc cho Khách hàng
        communicationService.saveComms(
                savedCustomer.getId(),
                CommunicationDetail.ParentType.CUSTOMER,
                request.getMainPhone(),
                request.getEmailOfficial(),
                request.getFax()
        );

        // Nếu là khách hàng cá nhân (B2C), tạo tự động 1 Contact tráng gương
        if (Boolean.FALSE.equals(request.getIsOrganization())) {
            Contact contact = createMirrorContactForB2C(savedCustomer, request);
            savedCustomer.setPrimaryContactId(contact.getId());
            customerRepository.save(savedCustomer);
        }

        return mapToResponse(savedCustomer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khách hàng với ID: " + id));
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Integer id, CustomerUpdateRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khách hàng với ID: " + id));

        mapUpdateRequestToEntity(request, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        // Cập nhật thông tin liên lạc (hạ cấp cái cũ, lưu cái mới)
        communicationService.updateComms(
                updatedCustomer.getId(),
                CommunicationDetail.ParentType.CUSTOMER,
                request.getMainPhone(),
                request.getEmailOfficial(),
                request.getFax()
        );

        // Nếu là B2C, update luôn vào Contact tráng gương
        if (Boolean.FALSE.equals(existingCustomer.getIsOrganization()) && existingCustomer.getPrimaryContactId() != null) {
            Contact contact = contactRepository.findById(existingCustomer.getPrimaryContactId()).orElse(null);
            if (contact != null) {
                contact.setPersonalPhone(request.getMainPhone());
                contact.setPersonalEmail(request.getEmailOfficial());
                contactRepository.save(contact);

                communicationService.updateComms(
                        contact.getId(),
                        CommunicationDetail.ParentType.CONTACT,
                        request.getMainPhone(),
                        request.getEmailOfficial(),
                        null
                );
            }
        }

        return mapToResponse(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khách hàng với ID: " + id));

        // 1. Xóa mềm Khách hàng
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);

        // 2. Xóa mềm DÂY CHUYỀN toàn bộ Người liên hệ (Contact) của Khách hàng này
        // Áp dụng cho cả B2C và B2B
        List<Contact> relatedContacts = contactRepository.findByCustomerId(id);
        for (Contact contact : relatedContacts) {
            contact.setDeletedAt(LocalDateTime.now());
            contactRepository.save(contact);
        }

    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Contact createMirrorContactForB2C(Customer customer, CustomerCreateRequest request) {
        Contact contact = new Contact();
        contact.setCustomer(customer);

        // Tách Họ và Tên từ Full Name
        String fullName = customer.getName().trim();
        int lastSpaceIndex = fullName.lastIndexOf(" ");
        if (lastSpaceIndex > 0) {
            contact.setLastName(fullName.substring(0, lastSpaceIndex).trim());
            contact.setFirstName(fullName.substring(lastSpaceIndex + 1).trim());
        } else {
            contact.setFirstName(fullName);
            contact.setLastName("");
        }

        contact.setJobTitle("Cá nhân");
        contact.setBirthday(customer.getFoundedDate());
        contact.setPersonalEmail(request.getEmailOfficial());
        contact.setPersonalPhone(request.getMainPhone());
        contact.setIsPrimary(true);

        Contact savedContact = contactRepository.save(contact);

        // Lưu thông tin liên lạc riêng cho Contact này
        communicationService.saveComms(
                savedContact.getId(),
                CommunicationDetail.ParentType.CONTACT,
                request.getMainPhone(),
                request.getEmailOfficial(),
                null
        );

        return savedContact;
    }

    private void mapRequestToEntity(CustomerCreateRequest request, Customer customer) {
        customer.setName(request.getName());
        customer.setShortName(request.getShortName());
        customer.setIsOrganization(request.getIsOrganization());
        customer.setTaxCode(request.getTaxCode());
        customer.setCitizenId(request.getCitizenId());
        customer.setFoundedDate(request.getFoundedDate());
        customer.setMainPhone(request.getMainPhone());
        customer.setEmailOfficial(request.getEmailOfficial());
        customer.setFax(request.getFax());
        customer.setWebsite(request.getWebsite());
        customer.setAddressCompany(request.getAddressCompany());
        customer.setAddressBilling(request.getAddressBilling());
        customer.setDescription(request.getDescription());
        customer.setSourceId(request.getSourceId());
        customer.setCampaignId(request.getCampaignId());
        customer.setPrimaryContactId(request.getPrimaryContactId());
        customer.setBranchId(request.getBranchId());
        customer.setProvinceId(request.getProvinceId());
        customer.setAssignedUserId(request.getAssignedUserId());

        if (request.getStatusId() != null) {
            customer.setStatus(customerStatusRepository.getReferenceById(request.getStatusId()));
        }
        if (request.getRankId() != null) {
            customer.setRank(customerRankRepository.getReferenceById(request.getRankId()));
        }
    }

    private void mapUpdateRequestToEntity(CustomerUpdateRequest request, Customer customer) {
        customer.setName(request.getName());
        customer.setShortName(request.getShortName());
        customer.setIsOrganization(request.getIsOrganization());
        customer.setTaxCode(request.getTaxCode());
        customer.setCitizenId(request.getCitizenId());
        customer.setFoundedDate(request.getFoundedDate());
        customer.setMainPhone(request.getMainPhone());
        customer.setEmailOfficial(request.getEmailOfficial());
        customer.setFax(request.getFax());
        customer.setWebsite(request.getWebsite());
        customer.setAddressCompany(request.getAddressCompany());
        customer.setAddressBilling(request.getAddressBilling());
        customer.setDescription(request.getDescription());
        customer.setSourceId(request.getSourceId());
        customer.setCampaignId(request.getCampaignId());
        customer.setPrimaryContactId(request.getPrimaryContactId());
        customer.setBranchId(request.getBranchId());
        customer.setProvinceId(request.getProvinceId());
        customer.setAssignedUserId(request.getAssignedUserId());

        if (request.getStatusId() != null) {
            customer.setStatus(customerStatusRepository.getReferenceById(request.getStatusId()));
        } else {
            customer.setStatus(null);
        }

        if (request.getRankId() != null) {
            customer.setRank(customerRankRepository.getReferenceById(request.getRankId()));
        } else {
            customer.setRank(null);
        }
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .name(customer.getName())
                .shortName(customer.getShortName())
                .isOrganization(customer.getIsOrganization())
                .taxCode(customer.getTaxCode())
                .citizenId(customer.getCitizenId())
                .foundedDate(customer.getFoundedDate())
                .website(customer.getWebsite())
                .emailOfficial(customer.getEmailOfficial())
                .mainPhone(customer.getMainPhone())
                .fax(customer.getFax())
                .addressCompany(customer.getAddressCompany())
                .addressBilling(customer.getAddressBilling())
                .description(customer.getDescription())
                .statusId(customer.getStatus() != null ? customer.getStatus().getId() : null)
                .statusName(customer.getStatus() != null ? customer.getStatus().getName() : null)
                .rankId(customer.getRank() != null ? customer.getRank().getId() : null)
                .rankName(customer.getRank() != null ? customer.getRank().getName() : null)
                .sourceId(customer.getSourceId())
                .campaignId(customer.getCampaignId())
                .primaryContactId(customer.getPrimaryContactId())
                .branchId(customer.getBranchId())
                .provinceId(customer.getProvinceId())
                .assignedUserId(customer.getAssignedUserId())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}