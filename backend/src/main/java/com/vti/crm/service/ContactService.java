package com.vti.crm.service;

import com.vti.crm.dto.request.ContactCreateRequest;
import com.vti.crm.dto.request.ContactUpdateRequest;
import com.vti.crm.dto.response.ContactResponse;
import com.vti.crm.entity.CommunicationDetail;
import com.vti.crm.entity.Contact;
import com.vti.crm.entity.Customer;
import com.vti.crm.repository.ContactRepository;
import com.vti.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;
    private final CommunicationService communicationService;

    @Transactional
    public ContactResponse createContact(ContactCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khách hàng với ID: " + request.getCustomerId()));

        Contact contact = new Contact();
        contact.setCustomer(customer);
        mapRequestToEntity(request, contact);

        Contact savedContact = contactRepository.save(contact);

        // Lưu thông tin liên lạc vào kho tổng
        communicationService.saveComms(
                savedContact.getId(),
                CommunicationDetail.ParentType.CONTACT,
                request.getPersonalPhone(),
                request.getPersonalEmail(),
                null
        );

        // Xử lý logic nếu đây là liên hệ chính
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            handlePrimaryContactLogic(customer, savedContact);
        }

        return mapToResponse(savedContact);
    }

    @Transactional
    public ContactResponse updateContact(Integer id, ContactUpdateRequest request) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Người liên hệ với ID: " + id));

        mapUpdateRequestToEntity(request, existingContact);
        Contact updatedContact = contactRepository.save(existingContact);

        // Cập nhật thông tin liên lạc cho Contact (Hạ cấp cái cũ, tạo cái mới)
        communicationService.updateComms(
                updatedContact.getId(),
                CommunicationDetail.ParentType.CONTACT,
                request.getPersonalPhone(),
                request.getPersonalEmail(),
                null
        );

        // Xử lý logic nếu có sự thay đổi về quyền Liên hệ chính
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            handlePrimaryContactLogic(updatedContact.getCustomer(), updatedContact);
        }
        // ==============================================================================
        // ĐỒNG BỘ NGƯỢC LẠI CUSTOMER (CHỈ ÁP DỤNG CHO B2C VÀ LÀ LIÊN HỆ CHÍNH)
        // ==============================================================================
        Customer customer = updatedContact.getCustomer();
        // Kiểm tra: Có Customer + Là B2C + Contact này đang là Contact chính
        if (customer != null
                && Boolean.FALSE.equals(customer.getIsOrganization())
                && updatedContact.getId().equals(customer.getPrimaryContactId())) {

            boolean needCustomerSync = false;

            // Nếu Phone thay đổi
            if (request.getPersonalPhone() != null && !request.getPersonalPhone().equals(customer.getMainPhone())) {
                customer.setMainPhone(request.getPersonalPhone());
                needCustomerSync = true;
            }

            // Nếu Email thay đổi
            if (request.getPersonalEmail() != null && !request.getPersonalEmail().equals(customer.getEmailOfficial())) {
                customer.setEmailOfficial(request.getPersonalEmail());
                needCustomerSync = true;
            }

            // Nếu có sự lệch pha, lưu lại Customer và update luôn kho liên lạc của Customer
            if (needCustomerSync) {
                customerRepository.save(customer);
                communicationService.updateComms(
                        customer.getId(),
                        CommunicationDetail.ParentType.CUSTOMER,
                        customer.getMainPhone(),
                        customer.getEmailOfficial(),
                        customer.getFax()
                );
            }
        }

        return mapToResponse(updatedContact);
    }

    public List<ContactResponse> getAllContacts() {
        return contactRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public List<ContactResponse> getContactsByCustomerId(Integer customerId) {
        return contactRepository.findByCustomerId(customerId).stream().map(this::mapToResponse).toList();
    }

    public ContactResponse getContactById(Integer id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Người liên hệ với ID: " + id));
        return mapToResponse(contact);
    }

    @Transactional
    public void deleteContact(Integer id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Người liên hệ với ID: " + id));

        Customer customer = contact.getCustomer();

        // Nếu xóa trúng người liên hệ chính, reset primary_contact_id của Công ty
        if (customer != null && contact.getId().equals(customer.getPrimaryContactId())) {
            customer.setPrimaryContactId(null);
            customerRepository.save(customer);
        }

        contact.setDeletedAt(LocalDateTime.now());
        contactRepository.save(contact);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private void handlePrimaryContactLogic(Customer customer, Contact newPrimaryContact) {
        // 1. Tìm tất cả các liên hệ hiện tại đang là Primary của khách hàng này
        List<Contact> currentPrimaries = contactRepository.findByCustomerIdAndIsPrimaryTrue(customer.getId());

        // 2. Hạ cấp họ xuống thành false (ngoại trừ chính người vừa được set)
        for (Contact c : currentPrimaries) {
            if (!c.getId().equals(newPrimaryContact.getId())) {
                c.setIsPrimary(false);
                contactRepository.save(c);
            }
        }

        // 3. Cập nhật ID này vào bảng Customer
        customer.setPrimaryContactId(newPrimaryContact.getId());
        customerRepository.save(customer);
    }

    private void mapRequestToEntity(ContactCreateRequest request, Contact contact) {
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setJobTitle(request.getJobTitle());
        contact.setBirthday(request.getBirthday());
        contact.setPersonalEmail(request.getPersonalEmail());
        contact.setPersonalPhone(request.getPersonalPhone());
        contact.setIsPrimary(request.getIsPrimary());
    }

    private void mapUpdateRequestToEntity(ContactUpdateRequest request, Contact contact) {
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setJobTitle(request.getJobTitle());
        contact.setBirthday(request.getBirthday());
        contact.setPersonalEmail(request.getPersonalEmail());
        contact.setPersonalPhone(request.getPersonalPhone());
        if (request.getIsPrimary() != null) {
            contact.setIsPrimary(request.getIsPrimary());
        }
    }

    private ContactResponse mapToResponse(Contact contact) {
        String fullName = (contact.getLastName() != null ? contact.getLastName() + " " : "") + contact.getFirstName();

        return ContactResponse.builder()
                .id(contact.getId())
                .customerId(contact.getCustomer() != null ? contact.getCustomer().getId() : null)
                .customerName(contact.getCustomer() != null ? contact.getCustomer().getName() : null)
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .fullName(fullName.trim())
                .jobTitle(contact.getJobTitle())
                .birthday(contact.getBirthday())
                .personalEmail(contact.getPersonalEmail())
                .personalPhone(contact.getPersonalPhone())
                .isPrimary(contact.getIsPrimary())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}