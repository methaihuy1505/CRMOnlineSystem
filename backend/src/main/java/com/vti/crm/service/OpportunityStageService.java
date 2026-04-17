package com.vti.crm.service;


import com.vti.crm.dto.request.OpportunityStageRequest;
import com.vti.crm.dto.response.OpportunityStageResponse;
import com.vti.crm.entity.OpportunityStage;
import com.vti.crm.exception.ResourceNotFoundException;
import com.vti.crm.repository.OpportunityStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpportunityStageService {

    private final OpportunityStageRepository repository;

    public OpportunityStageResponse create(OpportunityStageRequest request) {
        OpportunityStage stage = OpportunityStage.builder()
                .name(request.getName())
                .probabilityDefault(request.getProbabilityDefault())
                .sortOrder(request.getSortOrder())
                .isClosed(request.getIsClosed())
                .build();

        OpportunityStage saved = repository.save(stage);
        return mapToResponse(saved);
    }
    /* ================= UPDATE ================= */
    @Transactional
    public OpportunityStageResponse update(Integer id, OpportunityStageRequest request) {
        OpportunityStage stage = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stage not found with id: " + id));

        stage.setName(request.getName());
        stage.setProbabilityDefault(request.getProbabilityDefault());
        stage.setSortOrder(request.getSortOrder());
        stage.setIsClosed(request.getIsClosed());

        OpportunityStage updated = repository.save(stage);
        return mapToResponse(updated);
    }

    /* ================= DELETE ================= */
    @Transactional
    public void delete(Integer id) {
        OpportunityStage stage = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stage not found with id: " + id));

        repository.delete(stage);
    }
    public OpportunityStageResponse getById(Integer id) {
        OpportunityStage stage = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stage not found with id: " + id));

        return mapToResponse(stage);
    }

    public List<OpportunityStageResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OpportunityStageResponse mapToResponse(OpportunityStage stage) {
        return OpportunityStageResponse.builder()
                .id(stage.getId())
                .name(stage.getName())
                .probabilityDefault(stage.getProbabilityDefault())
                .sortOrder(stage.getSortOrder())
                .isClosed(stage.getIsClosed())
                .build();
    }
}