package com.pillone.pillone.controller;

import com.pillone.pillone.model.Usuarios;
import com.pillone.pillone.repository.UsuariosRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosRepository usuariosRepository;

    public UsuariosController(
            UsuariosRepository usuariosRepository
    ){
        this.usuariosRepository=usuariosRepository;
    }

    @GetMapping
    public List<Usuarios> getAll(){
        return usuariosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Usuarios getById(
            @PathVariable Long id
    ){
        return usuariosRepository
                .findById(id)
                .orElse(null);
    }
}