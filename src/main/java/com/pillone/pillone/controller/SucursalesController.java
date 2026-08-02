package com.pillone.pillone.controller;

import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sucursales")
public class SucursalesController {

    @Autowired
    private SucursalesRepository sucursalesRepository;

    // 1. Listar todas las sucursales
    @GetMapping
    public String listarSucursales(Model model) {
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        return "sucursales/sucursales"; // Ruta del archivo HTML en templates
    }

    // 2. Mostrar formulario para nueva sucursal
    @GetMapping("/nuevo")
    public String nuevaSucursal(Model model) {
        model.addAttribute("sucursal", new Sucursales());
        return "sucursales/sucursalForm";
    }

    // 3. Mostrar formulario para editar sucursal existente
    @GetMapping("/editar/{id}")
    public String editarSucursal(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Sucursales sucursales = sucursalesRepository.findById(id).orElse(null);
        if (sucursales == null) {
            redirectAttributes.addFlashAttribute("error", "La sucursal solicitada no existe.");
            return "redirect:/sucursales";
        }
        model.addAttribute("sucursal", sucursales);
        return "sucursales/sucursalForm";
    }

    // 4. Guardar o actualizar una sucursal
    @PostMapping("/guardar")
    public String guardarSucursal(@ModelAttribute Sucursales sucursales, RedirectAttributes redirectAttributes) {
        sucursalesRepository.save(sucursales);
        redirectAttributes.addFlashAttribute("mensaje", "¡Sucursal guardada con éxito!");
        return "redirect:/sucursales";
    }

    // 5. Eliminar una sucursal
    @PostMapping("/eliminar/{id}")
    public String eliminarSucursal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sucursalesRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "¡Sucursal eliminada con éxito!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la sucursal porque está asociada a otros registros.");
        }
        return "redirect:/sucursales";
    }
}