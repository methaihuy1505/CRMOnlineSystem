package com.vti.crm.service;





import com.vti.crm.entity.Products;
import com.vti.crm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Products create(Products product) {
        validateProduct(product);
        prepareData(product);
        return productRepository.save(product);
    }

    public Products update(Integer id, Products newData) {
        Products existing = getProduct(id);
        mapUpdate(existing, newData);
        return productRepository.save(existing);
    }

    public void delete(Integer id) {
        Products product = getProduct(id);
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    // ================= PRIVATE METHODS =================

    private Products getProduct(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private void validateProduct(Products product) {
        if (product.getProductCode() == null) {
            throw new RuntimeException("Product code is required");
        }
    }

    private void prepareData(Products product) {
        if (product.getVatRate() == null) {
            product.setVatRate(product.getVatRate());
        }
    }

    private void mapUpdate(Products existing, Products newData) {
        existing.setName(newData.getName());
        existing.setBasePrice(newData.getBasePrice());
        existing.setVatRate(newData.getVatRate());
        existing.setDepositOverride(newData.getDepositOverride());
        existing.setDescription(newData.getDescription());
    }
}