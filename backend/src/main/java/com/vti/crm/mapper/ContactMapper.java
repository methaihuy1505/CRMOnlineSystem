package com.vti.crm.mapper;

import com.vti.crm.dto.request.ContactRequest;
import com.vti.crm.dto.response.ContactResponse;
import com.vti.crm.entity.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public Contact toEntity(ContactRequest request) {
        if (request == null) return null;
        return Contact.builder()
                .customerId(request.getCustomerId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .jobTitle(request.getJobTitle())
                .birthday(request.getBirthday())
                .personalEmail(request.getPersonalEmail())
                .personalPhone(request.getPersonalPhone())
                .isPrimary(request.getIsPrimary())
                .build();
    }

    public ContactResponse toResponse(Contact entity) {
        if (entity == null) return null;
        ContactResponse response = new ContactResponse();
        response.setId(entity.getId());
        response.setCustomerId(entity.getCustomerId());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setJobTitle(entity.getJobTitle());
        response.setBirthday(entity.getBirthday());
        response.setPersonalEmail(entity.getPersonalEmail());
        response.setPersonalPhone(entity.getPersonalPhone());
        response.setIsPrimary(entity.getIsPrimary());
        return response;
    }

    public void updateEntity(Contact entity, ContactRequest request) {
        if (request.getFirstName() != null) entity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) entity.setLastName(request.getLastName());
        if (request.getJobTitle() != null) entity.setJobTitle(request.getJobTitle());
        if (request.getBirthday() != null) entity.setBirthday(request.getBirthday());
        if (request.getIsPrimary() != null) entity.setIsPrimary(request.getIsPrimary());
    }
}
