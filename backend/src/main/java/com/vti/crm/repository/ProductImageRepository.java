package com.vti.crm.repository;

import com.vti.crm.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Integer productId);

    void deleteByProductId(Integer productId);

    // Lấy danh sách ảnh theo Product ID
    List<ProductImage> findByProductId(Integer productId);
}