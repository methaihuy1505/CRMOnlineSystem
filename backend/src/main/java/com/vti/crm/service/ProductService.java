package com.vti.crm.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vti.crm.dto.request.ProductRequest;
import com.vti.crm.dto.response.ProductResponse;
import com.vti.crm.entity.Product;
import com.vti.crm.entity.ProductCategory;
import com.vti.crm.entity.ProductImage;
import com.vti.crm.entity.Uom;
import com.vti.crm.repository.ProductCategoryRepository;
import com.vti.crm.repository.ProductImageRepository;
import com.vti.crm.repository.ProductRepository;
import com.vti.crm.repository.UomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UomRepository uomRepository;
    private final Cloudinary cloudinary;

    // ================= CREATE =================
    public ProductResponse create(ProductRequest request,
                                  List<MultipartFile> images) {

        validateDuplicateCode(request.getProductCode());
        Product product = mapToEntity(request);
        productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            saveImages(product, images);
        }

        return mapToResponse(product);
    }

    // ================= UPDATE =================
    public ProductResponse update(Integer id,
                                  ProductRequest request,
                                  List<MultipartFile> images) {

        Product product = findById(id);
        validateDuplicateCodeForUpdate(id, request.getProductCode());
        mapForUpdate(product, request);

        if (images != null && !images.isEmpty()) {
            productImageRepository.deleteByProductId(id);
            saveImages(product, images);
        }

        return mapToResponse(productRepository.save(product));
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public ProductResponse getById(Integer id) {
        return mapToResponse(findById(id));
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= DELETE (SOFT DELETE) =================
    public void delete(Integer id) {
        Product product = findById(id);
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    // ================= PRIVATE METHODS =================

    private Product findById(Integer id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + id));
    }

    private void validateDuplicateCode(String code) {
        if (productRepository.existsByProductCodeAndIsDeletedFalse(code)) {
            throw new RuntimeException("Product code already exists");
        }
    }

    private void validateDuplicateCodeForUpdate(Integer id, String code) {
        productRepository.findByProductCodeAndIsDeletedFalse(code)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("Product code already exists");
                    }
                });
    }

    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .category(getCategory(request.getCategoryId()))
                .uom(getUom(request.getUomId()))
                .productType(request.getProductType())
                .basePrice(request.getBasePrice())
                .vatRate(request.getVatRate())
                .depositOverride(request.getDepositOverride())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .build();
    }

    private void mapForUpdate(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setCategory(getCategory(request.getCategoryId()));
        product.setUom(getUom(request.getUomId()));
        product.setProductType(request.getProductType());
        product.setBasePrice(request.getBasePrice());
        product.setVatRate(request.getVatRate());
        product.setDepositOverride(request.getDepositOverride());
        product.setImageUrl(request.getImageUrl());
        product.setDescription(request.getDescription());
    }

    private void saveImages(Product product, List<MultipartFile> files) {
        int index = 0;
        for (MultipartFile file : files) {
            validateFile(file);
            String url = uploadToCloudinary(file);

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(url);
            image.setSortOrder(index++);

            productImageRepository.save(image);
        }
    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "crm/products",
                            "resource_type", "image"
                    )
            );
            return result.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must not be empty");
        }

        String type = file.getContentType();
        if (type == null ||
                !(type.equals("image/jpeg") ||
                        type.equals("image/png") ||
                        type.equals("image/jpg") ||
                        type.equals("image/webp"))) {
            throw new RuntimeException("Only image files are allowed");
        }
    }

    private ProductResponse mapToResponse(Product product) {
        List<String> imageUrls = productImageRepository
                .findByProductIdOrderBySortOrderAsc(product.getId())
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .uomId(product.getUom() != null ? product.getUom().getId() : null)
                .uomName(product.getUom() != null ? product.getUom().getName() : null)
                .productType(product.getProductType())
                .basePrice(product.getBasePrice())
                .vatRate(product.getVatRate())
                .depositOverride(product.getDepositOverride())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .images(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductCategory getCategory(Integer id) {
        if (id == null) return null;
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private Uom getUom(Integer id) {
        if (id == null) return null;
        return uomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UOM not found"));
    }
}