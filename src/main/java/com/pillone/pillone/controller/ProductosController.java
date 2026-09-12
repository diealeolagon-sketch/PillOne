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
    public Productos getById(@PathVariable Long id) {
        return productosRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Productos create(@RequestBody Productos producto) {
        validarYCalcularEmpaque(producto);
        return productosRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Productos update(@PathVariable Long id, @RequestBody Productos producto) {
        return productosRepository.findById(id).map(productoExistente -> {
            producto.setIdProducto(id);
            validarYCalcularEmpaque(producto);
            return productosRepository.save(producto);
        }).orElseGet(() -> {
            producto.setIdProducto(id);
            validarYCalcularEmpaque(producto);
            return productosRepository.save(producto);
        });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productosRepository.deleteById(id);
    }

    /**
     * Método auxiliar para validar y calcular automáticamente las unidades por empaque
     * basándose en la multiplicación de los sellos por caja y las unidades por sello.
     */
    private void validarYCalcularEmpaque(Productos producto) {
        if (producto.getSellosPorCaja() == null || producto.getSellosPorCaja() <= 0) {
            producto.setSellosPorCaja(1);
        }
        if (producto.getUnidadesPorSello() == null || producto.getUnidadesPorSello() <= 0) {
            producto.setUnidadesPorSello(1);
        }

        // Cálculo automático del total de unidades por caja/empaque
        int totalUnidades = producto.getSellosPorCaja() * producto.getUnidadesPorSello();
        producto.setUnidadesPorEmpaque(totalUnidades);
    }
}