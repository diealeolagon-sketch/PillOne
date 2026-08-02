package com.pillone.pillone.view;

import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SucursalesView {

    @Autowired
    private SucursalesRepository sucursalesRepository;

    // LISTA DE SUCURSALES
    @GetMapping("/view/sucursales")
    public String lista(Model model) {
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        return "sucursales/sucursales";
    }

    // FORMULARIO NUEVA SUCURSAL
    @GetMapping("/view/sucursales/form")
    public String form(Model model) {
        model.addAttribute("sucursal", new Sucursales());
        return "sucursales/sucursalesForm";
    }

    // GUARDAR (CREAR / ACTUALIZAR)
    @PostMapping("/view/sucursales/save")
    public String save(@ModelAttribute Sucursales sucursales, RedirectAttributes ra) {
        sucursalesRepository.save(sucursales);
        ra.addFlashAttribute("mensaje", "Sucursal registrada con éxito");
        return "redirect:/view/sucursales";
    }

    // EDITAR SUCURSAL
    @GetMapping("/view/sucursales/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Sucursales sucursales = sucursalesRepository.findById(id).orElse(null);
        if (sucursales == null) {
            ra.addFlashAttribute("error", "La sucursal solicitada no existe.");
            return "redirect:/view/sucursales";
        }
        model.addAttribute("sucursal", sucursales);
        return "sucursales/sucursalesForm";
    }

    // ELIMINAR SUCURSAL
    @PostMapping("/view/sucursales/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sucursalesRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Sucursal eliminada con éxito");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la sucursal porque está asociada a otros registros.");
        }
        return "redirect:/view/sucursales";
    }
}