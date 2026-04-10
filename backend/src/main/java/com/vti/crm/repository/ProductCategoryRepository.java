package com.vti.crm.repository;

import com.vti.crm.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByIdAndIsDeletedFalse(Integer id);

    List<ProductCategory> findByIsDeletedFalse();

    boolean existsByNameAndIsDeletedFalse(String name);

    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.isDeleted = true WHERE pc.id = :id")
    void softDeleteById(Integer id);
}