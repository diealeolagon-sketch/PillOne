package com.pillone.pillone.controller;

import com.pillone.pillone.model.Clientes;
import com.pillone.pillone.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    /**
     * Búsqueda pensada para el POS cuando existan muchos clientes.
     * Busca por nombre, documento/cédula, teléfono o correo y limita resultados.
     */
    @GetMapping("/buscar")
    public List<Clientes> buscar(@RequestParam(defaultValue = "") String q)
    {
        String termino = q == null ? "" : q.trim();
        if (termino.length() < 2)
        {
            return Collections.emptyList();
        }

        return clientesRepository.buscar(termino, PageRequest.of(0, 30));
    }

    @GetMapping("/{id}")
    public Clientes getById(@PathVariable long id)
    {
        return clientesRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Clientes update(@PathVariable long id, @RequestBody Clientes clientes)
    {
        clientes.setId_cliente(id);
        return clientesRepository.save(clientes);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id)
    {
        clientesRepository.deleteById(id);
    }
}
