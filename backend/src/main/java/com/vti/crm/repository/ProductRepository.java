package com.vti.crm.repository;



import com.vti.crm.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Integer> {

    Optional<Products> findByProductCodeAndIsDeletedFalse(String productCode);
}