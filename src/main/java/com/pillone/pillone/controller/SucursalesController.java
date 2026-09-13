package com.pillone.pillone.controller;

import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalesController {

    private final SucursalesRepository sucursalesRepository;

    public SucursalesController(SucursalesRepository sucursalesRepository){
        this.sucursalesRepository=sucursalesRepository;
    }

    @GetMapping
    public List<Sucursales> getAll(){
        return sucursalesRepository.findAllByOrderByEstadoAscNombreAsc();
    }

    @GetMapping("/{id}")
    public Sucursales getById(@PathVariable Long id){
        return sucursalesRepository.findById(id).orElse(null);
    }
}