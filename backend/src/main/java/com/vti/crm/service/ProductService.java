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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ProductResponse create(ProductRequest request, List<MultipartFile> images) {
        validateDuplicateCode(request.getProductCode());
        Product product = mapToEntity(request);

        // Lưu sản phẩm trước để có ID
        productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            // Lưu danh sách ảnh và lấy URL của ảnh đầu tiên
            String firstImageUrl = saveImages(product, images);
            // Cập nhật ảnh đại diện cho sản phẩm
            product.setImageUrl(firstImageUrl);
            productRepository.save(product);
        }

        return mapToResponse(product);
    }

    // ================= UPDATE =================
    @Transactional
    public ProductResponse update(Integer id,
                                  ProductRequest request,
                                  List<MultipartFile> images) {

        // 1. Tìm sản phẩm hiện tại
        Product product = findById(id);

        // 2. Kiểm tra trùng mã sản phẩm
        validateDuplicateCodeForUpdate(id, request.getProductCode());

        // 3. Cập nhật dữ liệu từ request
        mapForUpdate(product, request);

        // 4. Xử lý ảnh nếu có
        if (images != null && !images.isEmpty()) {
            replaceProductImages(product, images);
        }

        // 5. Lưu và trả về kết quả
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }
    private void replaceProductImages(Product product,
                                      List<MultipartFile> images) {

        // 1. Lấy danh sách ảnh cũ
        List<ProductImage> oldImages =
                productImageRepository.findByProductId(product.getId());

        // 2. Xóa ảnh trên Cloudinary
        deleteImagesFromCloudinary(oldImages);

        // 3. Xóa ảnh trong Database
        productImageRepository.deleteByProductId(product.getId());

        // 4. Lưu ảnh mới
        String firstImageUrl = saveImages(product, images);

        // 5. Cập nhật ảnh đại diện
        product.setImageUrl(firstImageUrl);
    }
    private void deleteImagesFromCloudinary(List<ProductImage> images) {
        for (ProductImage image : images) {
            try {
                String publicId = extractPublicId(image.getImageUrl());
                if (publicId != null) {
                    cloudinary.uploader().destroy(
                            publicId,
                            com.cloudinary.utils.ObjectUtils.emptyMap()
                    );
                }
            } catch (Exception e) {
//                log.error("Failed to delete image on Cloudinary: {}",
//                        image.getImageUrl(), e);
            }
        }
    }
    private String extractPublicId(String url) {
        try {
            if (url == null || !url.contains("/upload/")) {
                return null;
            }

            // Ví dụ URL:
            // https://res.cloudinary.com/.../upload/v123456/crm/products/image.jpg
            String temp = url.split("/upload/")[1];

            // Loại bỏ version (v123456/)
            temp = temp.substring(temp.indexOf("/") + 1);

            // Loại bỏ phần mở rộng (.jpg, .png,...)
            return temp.substring(0, temp.lastIndexOf("."));
        } catch (Exception e) {
//            log.error("Error extracting public_id from URL: {}", url, e);
            return null;
        }
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

    private String saveImages(Product product, List<MultipartFile> files) {
        String firstUrl = null;
        int index = 0;
        for (MultipartFile file : files) {
            validateFile(file);
            String url = uploadToCloudinary(file);

            // Lưu lại URL của tấm hình đầu tiên
            if (index == 0) firstUrl = url;

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(url);
            image.setSortOrder(index++);

            productImageRepository.save(image);
        }
        return firstUrl; // Trả về để dùng làm ảnh đại diện
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