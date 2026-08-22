package com.pillone.pillone.controller;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.repository.ComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class ComprasController
{
    @Autowired
    private ComprasRepository comprasRepository;

    @GetMapping
    public List<Compras> getAll()
    {
        return comprasRepository.findAll();
    }

    @GetMapping("/{id}")
    public Compras getById(@PathVariable Integer id)
    {
        return comprasRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Compras update(@PathVariable long id, @RequestBody Compras compras)
    {
        compras.setIdCompra(id);
        return comprasRepository.save(compras);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id)
    {
        comprasRepository.deleteById(id);
    }
}