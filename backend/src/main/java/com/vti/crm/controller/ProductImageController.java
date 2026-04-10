package com.vti.crm.controller;

import com.vti.crm.entity.ProductImage;
import com.vti.crm.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

//    @PostMapping("/upload/{productId}")
//    public ProductImage uploadImage(
//            @PathVariable Integer productId,
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(value = "sortOrder", required = false) Integer sortOrder) {
//
//        return productImageService.uploadImage(productId, file, sortOrder);
//    }

    @PostMapping("/upload-multiple/{productId}")
    public List<ProductImage> uploadMultiple(
            @PathVariable Integer productId,
            @RequestParam("files") List<MultipartFile> files) {

        return productImageService.uploadMultipleImages(productId, files);
    }

//    @GetMapping("/product/{productId}")
//    public List<ProductImage> getImages(@PathVariable Integer productId) {
//        return productImageService.getImagesByProduct(productId);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Integer id) {
//        productImageService.deleteImage(id);
//    }

//    @PutMapping("/replace/{productId}")
//    public List<ProductImage> replaceImages(
//            @PathVariable Integer productId,
//            @RequestParam("files") List<MultipartFile> files) {
//
//        return productImageService.replaceImages(productId, files);
//    }
}