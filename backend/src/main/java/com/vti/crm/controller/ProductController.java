package com.vti.crm.controller;

import com.vti.crm.dto.request.ProductRequest;
import com.vti.crm.dto.response.ProductResponse;
import com.vti.crm.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // ================= CREATE (JSON ONLY) =================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse createJson(
            @Valid @RequestBody ProductRequest request) {
        return productService.create(request, null);
    }

    // ================= CREATE (MULTIPART WITH IMAGES) =================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse createMultipart(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false)
            List<MultipartFile> images) throws IOException {

        ProductRequest request =
                objectMapper.readValue(productJson, ProductRequest.class);

        return productService.create(request, images);
    }

    // ================= UPDATE =================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse update(
            @PathVariable Integer id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false)
            List<MultipartFile> images) throws IOException {

        ProductRequest request =
                objectMapper.readValue(productJson, ProductRequest.class);

        return productService.update(id, request, images);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    // ================= GET ALL =================
    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAll();
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }
}