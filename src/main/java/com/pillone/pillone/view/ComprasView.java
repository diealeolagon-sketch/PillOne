//by Jacob Mafla
package com.pillone.pillone.view;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.repository.ComprasRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComprasView {

    @Autowired
    private ComprasRepository repo;

    // LISTAR
    @GetMapping("/view/compras")
    public String lista(Model model) {
        model.addAttribute("compras", repo.findAll());
        return "compras/compras";
    }

    // FORMULARIO (Crear)
    @GetMapping("/view/compras/form")
    public String form(Model model) {
        model.addAttribute("compra", new Compras());
        return "compras/comprasForm";
    }

    // GUARDAR
    @PostMapping("/view/compras/save")
    public String save(@Valid @ModelAttribute Compras compra, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Verifica los campos obligatorios.");
            return "redirect:/view/compras/form";
        }
        repo.save(compra);
        ra.addFlashAttribute("mensaje", "Orden de compra guardada correctamente");
        return "redirect:/view/compras";
    }

    // EDITAR
    @GetMapping("/view/compras/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("compra", repo.findById(id).orElse(new Compras()));
        return "compras/comprasForm";
    }

    // ELIMINAR
    @PostMapping("/view/compras/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Orden de compra eliminada");
        return "redirect:/view/compras";
    }
}