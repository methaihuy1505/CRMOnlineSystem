package com.vti.crm.service;

import com.vti.crm.entity.Uom;
import com.vti.crm.repository.UomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UomService {

    private final UomRepository uomRepository;

    // ================= CREATE =================
    public Uom create(Uom uom) {
        validateForCreate(uom);
        setDefaultStatus(uom);
        return uomRepository.save(uom);
    }

    // ================= UPDATE =================
    public Uom update(Integer id, Uom uom) {
        Uom existing = getById(id);
        validateForUpdate(id, uom);
        mapForUpdate(existing, uom);
        return uomRepository.save(existing);
    }

    // ================= DELETE =================
    public void delete(Integer id) {
        Uom uom = getById(id);
        uomRepository.delete(uom); // Hard delete theo thiết kế DB
    }

    // ================= READ =================
    @Transactional(readOnly = true)
    public Uom getById(Integer id) {
        return uomRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("UOM not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Uom> getAll() {
        return uomRepository.findAll();
    }

    // ================= PRIVATE METHODS =================

    private void validateForCreate(Uom uom) {
        if (uom.getCode() == null || uom.getCode().trim().isEmpty()) {
            throw new RuntimeException("UOM code must not be empty");
        }

        if (uom.getName() == null || uom.getName().trim().isEmpty()) {
            throw new RuntimeException("UOM name must not be empty");
        }

        if (uomRepository.existsByCode(uom.getCode())) {
            throw new RuntimeException("UOM code already exists");
        }
    }

    private void validateForUpdate(Integer id, Uom uom) {
        if (uom.getCode() != null) {
            uomRepository.findByCode(uom.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("UOM code already exists");
                }
            });
        }
    }

    private void mapForUpdate(Uom existing, Uom newData) {
        if (newData.getCode() != null) {
            existing.setCode(newData.getCode());
        }

        if (newData.getName() != null) {
            existing.setName(newData.getName());
        }

        if (newData.getStatus() != null) {
            existing.setStatus(newData.getStatus());
        }
    }

    private void setDefaultStatus(Uom uom) {
        if (uom.getStatus() == null) {
            uom.setStatus(Uom.Status.ACTIVE);
        }
    }
}