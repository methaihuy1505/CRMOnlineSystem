package com.vti.crm.service;

import com.vti.crm.dto.request.OpportunityStatusRequest;
import com.vti.crm.dto.response.OpportunityStatusResponse;
import com.vti.crm.entity.OpportunityStatus;
import com.vti.crm.exception.ResourceNotFoundException;
import com.vti.crm.repository.OpportunityStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OpportunityStatusService {

    private final OpportunityStatusRepository repository;

    @Transactional(readOnly = true)
    public List<OpportunityStatusResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OpportunityStatusResponse findById(Integer id) {
        OpportunityStatus entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OpportunityStatus not found with id: " + id));
        return mapToResponse(entity);
    }

    public OpportunityStatusResponse create(OpportunityStatusRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Status code already exists!");
        }

        OpportunityStatus entity = mapToEntity(request, new OpportunityStatus());
        return mapToResponse(repository.save(entity));
    }

    public OpportunityStatusResponse update(Integer id, OpportunityStatusRequest request) {
        OpportunityStatus entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OpportunityStatus not found with id: " + id));

        mapToEntity(request, entity);
        return mapToResponse(repository.save(entity));
    }

    public void delete(Integer id) {
        OpportunityStatus entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OpportunityStatus not found with id: " + id));
        repository.delete(entity);
    }

    private OpportunityStatus mapToEntity(OpportunityStatusRequest request,
                                          OpportunityStatus entity) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setIsFinal(request.getIsFinal());
        return entity;
    }

    private OpportunityStatusResponse mapToResponse(OpportunityStatus entity) {
        return OpportunityStatusResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .isFinal(entity.getIsFinal())
                .build();
    }
}