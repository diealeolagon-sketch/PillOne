package com.pillone.pillone.controller;

import com.pillone.pillone.model.Proveedores;
import com.pillone.pillone.repository.ProveedoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedoresController {

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @GetMapping
    public List<Proveedores> getAll(){
        return proveedoresRepository.findAll();
    }

    @GetMapping("/{id}")
    public Proveedores getById(@PathVariable Integer id){
        return proveedoresRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Proveedores create(@RequestBody Proveedores proveedor){
        return proveedoresRepository.save(proveedor);
    }

    @PutMapping("/{id}")
    public Proveedores update(
            @PathVariable Integer id,
            @RequestBody Proveedores proveedor
    ){
        proveedor.setId_proveedor(id);
        return proveedoresRepository.save(proveedor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        proveedoresRepository.deleteById(id);
    }
}