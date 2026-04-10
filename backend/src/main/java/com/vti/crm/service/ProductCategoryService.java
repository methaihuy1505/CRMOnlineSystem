package com.vti.crm.service;

import com.vti.crm.dto.request.ProductCategoryRequest;
import com.vti.crm.dto.response.ProductCategoryResponse;
import com.vti.crm.entity.ProductCategory;
import com.vti.crm.repository.ProductCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCategoryService {

    private final ProductCategoryRepository repository;

    public ProductCategoryResponse create(ProductCategoryRequest request) {
        validateDuplicateName(request.getName());
        ProductCategory entity = mapToEntity(request);
        return mapToResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ProductCategoryResponse getById(Integer id) {
        return mapToResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAll() {
        return repository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductCategoryResponse update(Integer id, ProductCategoryRequest request) {
        ProductCategory entity = findById(id);
        validateDuplicateNameForUpdate(id, request.getName());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return mapToResponse(repository.save(entity));
    }

    public void delete(Integer id) {
        findById(id);
        repository.softDeleteById(id);
    }

    // ================= PRIVATE METHODS =================

    private ProductCategory findById(Integer id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product category not found with id: " + id));
    }

    private void validateDuplicateName(String name) {
        if (repository.existsByNameAndIsDeletedFalse(name)) {
            throw new IllegalArgumentException("Product category name already exists");
        }
    }

    private void validateDuplicateNameForUpdate(Integer id, String name) {
        repository.findByIsDeletedFalse().stream()
                .filter(pc -> !pc.getId().equals(id))
                .filter(pc -> pc.getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(pc -> {
                    throw new IllegalArgumentException("Product category name already exists");
                });
    }

    private ProductCategory mapToEntity(ProductCategoryRequest request) {
        return ProductCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isDeleted(false)
                .build();
    }

    private ProductCategoryResponse mapToResponse(ProductCategory entity) {
        return ProductCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}