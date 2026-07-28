package com.pillone.pillone.controller;
import com.pillone.pillone.model.Clientes;
import com.pillone.pillone.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController
{
    @Autowired
    private ClientesRepository clientesRepository;

    @GetMapping
    public List<Clientes> getAll()
    {
        return clientesRepository.findAll();

    }
    @GetMapping("/{id}")
    public Clientes getById(long id)
    {
        return clientesRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Clientes update(@PathVariable long id, @RequestBody Clientes clientes)
    {
        clientes.setId_bitacora(id);
        return clientesRepository.save(clientes);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id)
    {
        clientesRepository.deleteById(id);
    }


}
