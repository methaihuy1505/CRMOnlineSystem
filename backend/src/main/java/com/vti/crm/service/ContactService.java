package com.vti.crm.service;

import com.vti.crm.dto.request.ContactRequest;
import com.vti.crm.dto.response.ContactResponse;
import com.vti.crm.entity.Contact;
import com.vti.crm.exception.AppException;
import com.vti.crm.exception.ErrorCode;
import com.vti.crm.mapper.ContactMapper;
import com.vti.crm.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts() {
        return contactRepository.findAll().stream()
                .map(contactMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(Integer id) {
        var contact = contactRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CONTACT_NOT_FOUND));
        return contactMapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse createContact(ContactRequest request) {
        var contact = contactMapper.toEntity(request);
        if (contact.getIsPrimary() == null) {
            contact.setIsPrimary(false);
        }
        var savedContact = contactRepository.save(contact);
        return contactMapper.toResponse(savedContact);
    }

    @Transactional
    public ContactResponse updateContact(Integer id, ContactRequest request) {
        var oldContact = contactRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CONTACT_NOT_FOUND));

        boolean emailChanged = request.getPersonalEmail() != null && !request.getPersonalEmail().equals(oldContact.getPersonalEmail());
        boolean phoneChanged = request.getPersonalPhone() != null && !request.getPersonalPhone().equals(oldContact.getPersonalPhone());

        if (emailChanged || phoneChanged) {
            return createNewPrimaryContact(oldContact, request);
        } else {
            contactMapper.updateEntity(oldContact, request);
            return contactMapper.toResponse(contactRepository.save(oldContact));
        }
    }

    private ContactResponse createNewPrimaryContact(Contact oldContact, ContactRequest request) {
        // Đổi số cũ thành isPrimary = false
        oldContact.setIsPrimary(false);
        contactRepository.save(oldContact);

        // Tạo bản ghi mới cho số chính
        var newContact = new Contact();
        newContact.setCustomerId(oldContact.getCustomerId());
        newContact.setFirstName(request.getFirstName() != null ? request.getFirstName() : oldContact.getFirstName());
        newContact.setLastName(request.getLastName() != null ? request.getLastName() : oldContact.getLastName());
        newContact.setJobTitle(request.getJobTitle() != null ? request.getJobTitle() : oldContact.getJobTitle());
        newContact.setBirthday(request.getBirthday() != null ? request.getBirthday() : oldContact.getBirthday());
        newContact.setPersonalEmail(request.getPersonalEmail() != null ? request.getPersonalEmail() : oldContact.getPersonalEmail());
        newContact.setPersonalPhone(request.getPersonalPhone() != null ? request.getPersonalPhone() : oldContact.getPersonalPhone());
        newContact.setIsPrimary(true);

        var savedNewContact = contactRepository.save(newContact);
        return contactMapper.toResponse(savedNewContact);
    }
}
