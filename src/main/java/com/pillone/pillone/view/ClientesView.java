package com.odin.odin.view;

import com.odin.odin.model.Clientes;
import com.odin.odin.repository.ClientesRepository;
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
        model.addAttribute("bitacoras", clientesRepository.findAll());

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
    public String save(@ModelAttribute Clientes bitacora,
                       RedirectAttributes ra)
    {
        clientesRepository.save(bitacora);

        ra.addFlashAttribute("mensaje",
                "Bitácora registrada con éxito");

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
    @PostMapping("/view/bitacoras/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra)
    {
        clientesRepository.deleteById(id);

        ra.addFlashAttribute("mensaje",
                "cliente eliminada con éxito");

        return "redirect:/view/clientes";
    }
}