package com.pillone.pillone.controller;

import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping
    public List<Productos> getAll() {
        return productosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Productos getById(@PathVariable Integer id) {
        return productosRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Productos create(@RequestBody Productos producto) {
        return productosRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Productos update(@PathVariable Integer id, @RequestBody Productos producto) {
        producto.setIdProducto(id);
        return productosRepository.save(producto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        productosRepository.deleteById(id);
    }
}