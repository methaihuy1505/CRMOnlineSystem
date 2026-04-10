package com.vti.crm.controller;

import com.vti.crm.dto.request.ContactRequest;
import com.vti.crm.dto.response.ContactResponse;
import com.vti.crm.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    List<ContactResponse> getAllContacts() {
        return contactService.getAllContacts();
    }

    @GetMapping("/{id}")
    ContactResponse getContactById(@PathVariable Integer id) {
        return contactService.getContactById(id);
    }

    @PostMapping
    ContactResponse createContact(@RequestBody ContactRequest request) {
        return contactService.createContact(request);
    }

    @PutMapping("/{id}")
    ContactResponse updateContact(@PathVariable Integer id, @RequestBody ContactRequest request) {
        return contactService.updateContact(id, request);
    }
}
