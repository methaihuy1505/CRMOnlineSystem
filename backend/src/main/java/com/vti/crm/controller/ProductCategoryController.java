package com.vti.crm.controller;

import com.vti.crm.dto.request.ProductCategoryRequest;
import com.vti.crm.dto.response.ProductCategoryResponse;
import com.vti.crm.entity.ProductCategory;
import com.vti.crm.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoryResponse create(
            @Valid @RequestBody ProductCategoryRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ProductCategoryResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ProductCategoryResponse> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ProductCategoryResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody ProductCategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}