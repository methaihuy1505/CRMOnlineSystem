package com.vti.crm.service;

import com.vti.crm.dto.request.OpportunityItemRequest;
import com.vti.crm.dto.response.OpportunityItemResponse;
import com.vti.crm.entity.*;
import com.vti.crm.exception.ResourceNotFoundException;
import com.vti.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OpportunityItemService {

    private final OpportunityItemRepository opportunityItemRepository;
    private final OpportunityRepository opportunityRepository;
    private final ProductRepository productRepository;
    private final OpportunityService opportunityService;
    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<OpportunityItemResponse> findAll() {
        return opportunityItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY OPPORTUNITY =================
    @Transactional(readOnly = true)
    public List<OpportunityItemResponse> findByOpportunityId(Integer opportunityId) {
        return opportunityItemRepository
                .findByOpportunityIdOrderByLineItemNumberAsc(opportunityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public OpportunityItemResponse findById(Integer id) {
        OpportunityItem item = opportunityItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Opportunity Item not found with id: " + id));
        return mapToResponse(item);
    }

    // ================= CREATE =================
    public OpportunityItemResponse create(OpportunityItemRequest request) {
        Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OpportunityItem item = new OpportunityItem();
        // Sử dụng hàm mapToEntity để tái sử dụng logic
        mapToEntity(request, item);

        calculateAmounts(item);
        OpportunityItem savedItem = opportunityItemRepository.save(item);

        // QUAN TRỌNG: Cập nhật lại tổng tiền bên Opportunity
        opportunityService.recalculateOpportunityTotal(opportunity);

        return mapToResponse(savedItem);
    }

    // ================= UPDATE =================
    @Transactional
    public OpportunityItemResponse update(Integer id, OpportunityItemRequest request) {
        OpportunityItem item = opportunityItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        // Map lại dữ liệu mới
        mapToEntity(request, item);
        calculateAmounts(item);

        OpportunityItem savedItem = opportunityItemRepository.save(item);

        // Cập nhật lại tổng tiền cho Opportunity liên quan
        opportunityService.recalculateOpportunityTotal(item.getOpportunity());

        return mapToResponse(savedItem);
    }

    // ================= DELETE =================
    public void delete(Integer id) {
        OpportunityItem item = opportunityItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Opportunity opportunity = item.getOpportunity();

        opportunityItemRepository.delete(item);

        // Sau khi xóa, phải tính lại tổng tiền ngay
        opportunityService.recalculateOpportunityTotal(opportunity);
    }
    // ================= MAPPING: REQUEST → ENTITY =================
    private void mapToEntity(OpportunityItemRequest request, OpportunityItem item) {

        Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + request.getOpportunityId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        item.setOpportunity(opportunity);
        item.setProduct(product);

        // Snapshot data
        item.setProductName(product.getName());
        item.setUomName(product.getUom() != null ? product.getUom().getName() : "N/A");

        item.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        item.setUnitPrice(request.getUnitPrice() != null
                ? request.getUnitPrice()
                : product.getBasePrice());

        item.setVatRate(request.getVatRate() != null
                ? request.getVatRate()
                : product.getVatRate());

        item.setDiscountRate(request.getDiscountRate());
        item.setLineItemNumber(request.getLineItemNumber());
        item.setNote(request.getNote());
    }

    // ================= MAPPING: ENTITY → RESPONSE =================
    private OpportunityItemResponse mapToResponse(OpportunityItem item) {
        return OpportunityItemResponse.builder()
                .id(item.getId())
                .opportunityId(item.getOpportunity().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .uomName(item.getUomName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .vatRate(item.getVatRate())
                .vatAmount(item.getVatAmount())
                .discountRate(item.getDiscountRate())
                .discountAmount(item.getDiscountAmount())
                .totalPrice(item.getTotalPrice())
                .finalLineTotal(item.getFinalLineTotal())
                .lineItemNumber(item.getLineItemNumber())
                .note(item.getNote())
                .createdAt(item.getCreatedAt())
                .build();
    }

    // ================= BUSINESS LOGIC =================
    private void calculateAmounts(OpportunityItem item) {
        BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
        BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal vatRate = item.getVatRate() != null ? item.getVatRate() : BigDecimal.ZERO;
        BigDecimal discountRate = item.getDiscountRate() != null ? item.getDiscountRate() : BigDecimal.ZERO;

        // 1. Total Price = Unit Price * Quantity
        BigDecimal totalPrice = unitPrice.multiply(quantity);

        // 2. Discount Amount = Total Price * (Discount Rate / 100)
        BigDecimal discountAmount = totalPrice.multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 3. Sau chiết khấu
        BigDecimal afterDiscount = totalPrice.subtract(discountAmount);

        // 4. VAT Amount = After Discount * (VAT Rate / 100)
        BigDecimal vatAmount = afterDiscount.multiply(vatRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 5. Final Line Total = After Discount + VAT
        BigDecimal finalTotal = afterDiscount.add(vatAmount);

        item.setTotalPrice(totalPrice);
        item.setDiscountAmount(discountAmount);
        item.setVatAmount(vatAmount);
        item.setFinalLineTotal(finalTotal);
    }
}
