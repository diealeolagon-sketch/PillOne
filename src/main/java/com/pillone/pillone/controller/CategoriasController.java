package com.pillone.pillone.controller;

import com.pillone.pillone.model.Categorias;
import com.pillone.pillone.repository.CategoriasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriasController {

    @Autowired
    private CategoriasRepository categoriasRepository;

    @GetMapping
    public List<Categorias> getAll(){
        return categoriasRepository.findAll();
    }

    @GetMapping("/{id}")
    public Categorias getById(@PathVariable Integer id){
        return categoriasRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Categorias create(@RequestBody Categorias categoria){
        categoria.setIdCategoria(null);
        return categoriasRepository.save(categoria);
    }

    @PutMapping("/{id}")
    public Categorias update(
            @PathVariable Integer id,
            @RequestBody Categorias categoria
    ){
        return categoriasRepository.findById(id)
                .map(existente->{
                    existente.setNombre(categoria.getNombre());
                    existente.setDescripcion(categoria.getDescripcion());
                    return categoriasRepository.save(existente);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        categoriasRepository.deleteById(id);
    }
}