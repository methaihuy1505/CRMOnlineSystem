package com.vti.crm.controller;

import com.vti.crm.entity.Uom;
import com.vti.crm.service.UomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/uoms")
@RequiredArgsConstructor
public class UomController {

    private final UomService uomService;

    @PostMapping
    public Uom create(@RequestBody Uom uom) {
        return uomService.create(uom);
    }

    @PutMapping("/{id}")
    public Uom update(@PathVariable Integer id, @RequestBody Uom uom) {
        return uomService.update(id, uom);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        uomService.delete(id);
    }

    @GetMapping("/{id}")
    public Uom getById(@PathVariable Integer id) {
        return uomService.getById(id);
    }

    @GetMapping
    public List<Uom> getAll() {
        return uomService.getAll();
    }
}