package com.vti.crm.service;

import com.vti.crm.dto.request.CustomerFeedbackRequest;
import com.vti.crm.dto.response.CustomerFeedbackResponse;
import com.vti.crm.entity.CustomerFeedback;
import com.vti.crm.mapper.CustomerFeedbackMapper;
import com.vti.crm.repository.CustomerFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerFeedbackService {

    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final CustomerFeedbackMapper customerFeedbackMapper;

    @Transactional(readOnly = true)
    public List<CustomerFeedbackResponse> getAllFeedbacks() {
        return customerFeedbackRepository.findAll().stream()
                .map(customerFeedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerFeedbackResponse getFeedbackById(Integer id) {
        CustomerFeedback feedback = customerFeedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CustomerFeedback not found with id: " + id));
        return customerFeedbackMapper.toResponse(feedback);
    }

    @Transactional
    public CustomerFeedbackResponse createFeedback(CustomerFeedbackRequest request) {
        CustomerFeedback feedback = customerFeedbackMapper.toEntity(request);
        CustomerFeedback savedFeedback = customerFeedbackRepository.save(feedback);
        return customerFeedbackMapper.toResponse(savedFeedback);
    }

    @Transactional
    public CustomerFeedbackResponse updateFeedback(Integer id, CustomerFeedbackRequest request) {
        CustomerFeedback existingFeedback = customerFeedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CustomerFeedback not found with id: " + id));
        
        existingFeedback.setCustomerId(request.getCustomerId());
        existingFeedback.setContactId(request.getContactId());
        existingFeedback.setSubject(request.getSubject());
        existingFeedback.setContent(request.getContent());
        if (request.getPriority() != null) {
            existingFeedback.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            existingFeedback.setStatus(request.getStatus());
        }
        existingFeedback.setAssignedTo(request.getAssignedTo());
        existingFeedback.setUpdatedAt(LocalDateTime.now());
        
        CustomerFeedback updatedFeedback = customerFeedbackRepository.save(existingFeedback);
        return customerFeedbackMapper.toResponse(updatedFeedback);
    }

    @Transactional
    public void deleteFeedback(Integer id) {
        if (!customerFeedbackRepository.existsById(id)) {
            throw new RuntimeException("CustomerFeedback not found with id: " + id);
        }
        customerFeedbackRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerFeedbackResponse> getFeedbacksByCustomerId(Integer customerId) {
        return customerFeedbackRepository.findByCustomerId(customerId).stream()
                .map(customerFeedbackMapper::toResponse)
                .collect(Collectors.toList());
    }
}