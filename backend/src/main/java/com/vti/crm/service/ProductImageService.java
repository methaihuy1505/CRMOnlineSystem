//package com.vti.crm.service;
//
//import com.cloudinary.Cloudinary;
//import com.vti.crm.entity.Product;
//import com.vti.crm.entity.ProductImage;
//import com.vti.crm.repository.ProductImageRepository;
//import com.vti.crm.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ProductImageService {
//
//    private final ProductImageRepository productImageRepository;
//    private final ProductRepository productRepository;
//    private final Cloudinary cloudinaryService;
//
//    // ================= UPLOAD SINGLE IMAGE =================
//    public ProductImage uploadImage(Integer productId,
//                                    MultipartFile file,
//                                    Integer sortOrder) {
//
//        validateFile(file);
//
//        Product product = getProduct(productId);
//        String imageUrl = "demo";     //gcsService.uploadFile(file);
//
//        ProductImage image = new ProductImage();
//        image.setProduct(product);
//        image.setImageUrl(imageUrl);
//        image.setSortOrder(sortOrder != null ? sortOrder : 0);
//
//        return productImageRepository.save(image);
//    }
//
//    // ================= UPLOAD MULTIPLE IMAGES =================
////    public List<ProductImage> uploadMultipleImages(Integer productId,
////                                                   List<MultipartFile> files) {
////
////        Product product = getProduct(productId);
////
////        return files.stream().map(file -> {
////            validateFile(file);
////            String imageUrl = gcsService.uploadFile(file);
////
////            ProductImage image = new ProductImage();
////            image.setProduct(product);
////            image.setImageUrl(imageUrl);
////            image.setSortOrder(0);
////            return productImageRepository.save(image);
////        }).toList();
////    }
//    public List<ProductImage> uploadMultipleImages(Integer productId,
//                                                   List<MultipartFile> files) {
//
//        Product product = getProduct(productId);
//
//        if (files == null || files.isEmpty()) {
//            throw new RuntimeException("File list must not be empty");
//        }
//
//        return files.stream().map(file -> {
//            validateFile(file);
//            String imageUrl = uploadToCloudinary(file);
//
//            ProductImage image = new ProductImage();
//            image.setProduct(product);
//            image.setImageUrl(imageUrl);
//            image.setSortOrder(0);
//
//            return productImageRepository.save(image);
//        }).toList();
//    }
//    // ================= GET IMAGES BY PRODUCT =================
//    @Transactional(readOnly = true)
//    public List<ProductImage> getImagesByProduct(Integer productId) {
//        return productImageRepository
//                .findByProductIdOrderBySortOrderAsc(productId);
//    }
//
//    // ================= DELETE IMAGE =================
//    public void deleteImage(Integer id) {
//        ProductImage image = productImageRepository.findById(id)
//                .orElseThrow(() ->
//                        new RuntimeException("Image not found with id: " + id));
//
//        productImageRepository.delete(image);
//    }
//
//    // ================= REPLACE IMAGES (XÓA SẠCH - XÂY LẠI) =================
//    public List<ProductImage> replaceImages(Integer productId,
//                                            List<MultipartFile> files) {
//
//        productImageRepository.deleteByProductId(productId);
//        return uploadMultipleImages(productId, files);
//    }
//
//    // ================= PRIVATE METHODS =================
//    private Product getProduct(Integer productId) {
//        return productRepository.findById(productId)
//                .orElseThrow(() ->
//                        new RuntimeException("Product not found with id: " + productId));
//    }
//
//    private void validateFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            throw new RuntimeException("File must not be empty");
//        }
//
//        String contentType = file.getContentType();
//        if (contentType == null ||
//                !(contentType.equals("image/jpeg")
//                        || contentType.equals("image/png")
//                        || contentType.equals("image/jpg")
//                        || contentType.equals("image/webp"))) {
//            throw new RuntimeException("Only image files are allowed");
//        }
//    }
//}

package com.vti.crm.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vti.crm.entity.Product;
import com.vti.crm.entity.ProductImage;
import com.vti.crm.repository.ProductImageRepository;
import com.vti.crm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    /**
     * Upload multiple images to Cloudinary and save to database
     */
    public List<ProductImage> uploadMultipleImages(Integer productId,
                                                   List<MultipartFile> files) {

        Product product = getProduct(productId);

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("File list must not be empty");
        }

        return files.stream()
                .map(file -> {
                    validateFile(file);
                    String imageUrl = uploadToCloudinary(file);

                    ProductImage image = new ProductImage();
                    image.setProduct(product);
                    image.setImageUrl(imageUrl);
                    image.setSortOrder(0);

                    return productImageRepository.save(image);
                })
                .collect(Collectors.toList());
    }

    // ================= PRIVATE METHODS =================

    private Product getProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + productId));
    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "crm/products",
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/webp"))) {
            throw new RuntimeException("Only image files are allowed");
        }
    }
}