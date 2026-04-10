package com.vti.crm.repository;

import com.vti.crm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByIdAndIsDeletedFalse(Integer id);

    Optional<Product> findByProductCodeAndIsDeletedFalse(String productCode);

    boolean existsByProductCodeAndIsDeletedFalse(String productCode);

    List<Product> findByIsDeletedFalse();
}