package com.pillone.pillone.controller;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.repository.ComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    @Autowired
    private ComprasRepository comprasRepository;

    @GetMapping
    public List<Compras> getAll(){
        return comprasRepository.findAll();
    }

    @GetMapping("/{id}")
    public Compras getById(
            @PathVariable Integer id
    ){
        return comprasRepository
                .findById(id)
                .orElse(null);
    }

    @PostMapping
    public Compras create(
            @RequestBody Compras compra
    ){
        return comprasRepository.save(compra);
    }

    @PutMapping("/{id}")
    public Compras update(
            @PathVariable Integer id,
            @RequestBody Compras compra
    ){
        compra.setIdCompra(id);
        return comprasRepository.save(compra);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Integer id
    ){
        comprasRepository.deleteById(id);
    }
}