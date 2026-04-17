package com.vti.crm.service;

import com.vti.crm.dto.request.LostReasonRequest;
import  com.vti.crm.dto.response.LostReasonResponse;
import com.vti.crm.entity.LostReason;
import com.vti.crm.exception.ResourceNotFoundException;
import com.vti.crm.repository.LostReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LostReasonService {

    private final LostReasonRepository repository;

    @Transactional(readOnly = true)
    public List<LostReasonResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LostReasonResponse findById(Integer id) {
        LostReason entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "LostReason not found with id: " + id));
        return mapToResponse(entity);
    }

    public LostReasonResponse create(LostReasonRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Lost reason code already exists!");
        }

        LostReason entity = mapToEntity(request, new LostReason());
        return mapToResponse(repository.save(entity));
    }

    public LostReasonResponse update(Integer id, LostReasonRequest request) {
        LostReason entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "LostReason not found with id: " + id));

        mapToEntity(request, entity);
        return mapToResponse(repository.save(entity));
    }

    public void delete(Integer id) {
        LostReason entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "LostReason not found with id: " + id));
        repository.delete(entity);
    }

    private LostReason mapToEntity(LostReasonRequest request, LostReason entity) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    private LostReasonResponse mapToResponse(LostReason entity) {
        return LostReasonResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}