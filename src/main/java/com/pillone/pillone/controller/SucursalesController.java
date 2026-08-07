package com.pillone.pillone.controller;

import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalesController
{
    @Autowired
    private SucursalesRepository sucursalesRepository;

    @GetMapping
    public List<Sucursales> getAll()
    {
        return sucursalesRepository.findAll();
    }

    @GetMapping("/{id}")
    public Sucursales getById(@PathVariable Long id) {
        return sucursalesRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Sucursales create(@RequestBody Sucursales sucursales)
    {
        return sucursalesRepository.save(sucursales);
    }

    @PutMapping("/{id}")
    public Sucursales update(@PathVariable long id, @RequestBody Sucursales sucursales)
    {
        sucursales.setIdSucursal(id); // <-- Cambiado de setId_sucursal a setIdSucursal
        return sucursalesRepository.save(sucursales);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id)
    {
        sucursalesRepository.deleteById(id);
    }
}