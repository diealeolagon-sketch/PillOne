package com.pillone.pillone.view;

import com.pillone.pillone.model.Clientes;
import com.pillone.pillone.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientesView
{
    @Autowired
    private ClientesRepository clientesRepository;

    // LISTA
    @GetMapping("/view/clientes")
    public String lista(Model model)
    {
        model.addAttribute("clientes", clientesRepository.findAll());

        return "clientes/clientes";
    }

    // FORMULARIO
    @GetMapping("/view/clientes/form")
    public String form(Model model)
    {
        model.addAttribute("cliente", new Clientes());

        return "Clientes/clientesForm";
    }

    // GUARDAR
    @PostMapping("/view/clientes/save")
    public String save(@ModelAttribute Clientes clientes,
                       RedirectAttributes ra)
    {
        clientesRepository.save(clientes);

        ra.addFlashAttribute("mensaje",
                "cliente registrada con éxito");

        return "redirect:/view/clientes";
    }

    // EDITAR
    @GetMapping("/view/clientes/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model)
    {
        Clientes clientes =
                clientesRepository.findById(id).orElse(null);

        model.addAttribute("clientes", clientes);

        return "clientes/clientesForm";
    }

    // ELIMINAR
    @PostMapping("/view/clientes/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra)
    {
        clientesRepository.deleteById(id);

        ra.addFlashAttribute("mensaje",
                "cliente eliminada con éxito");

        return "redirect:/view/clientes";
    }
}