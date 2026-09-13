package com.pillone.pillone.controller;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.repository.EmpleadosRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadosController {

    private final EmpleadosRepository empleadosRepository;

    public EmpleadosController(
            EmpleadosRepository empleadosRepository
    ){
        this.empleadosRepository=empleadosRepository;
    }

    @GetMapping
    public List<Empleados> getAll(){
        return empleadosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Empleados getById(
            @PathVariable Long id
    ){
        return empleadosRepository
                .findById(id)
                .orElse(null);
    }
}