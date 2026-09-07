package com.pillone.pillone.controller;

import com.pillone.pillone.model.Ventas;
import com.pillone.pillone.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/pos") // Ruta independiente que no choca con /view/ventas
public class VentasController {

    @Autowired
    private VentasRepository ventasRepository;

    // Ruta final: /view/pos/index
    @GetMapping
    public String verPos(Model model) {
        model.addAttribute("ventas", new Ventas());
        return "ventas/pos";
    }

    // Ruta final: /view/pos/guardar
    @PostMapping("/guardar")
    public String procesarVentas(@ModelAttribute Ventas ventas) {
        ventasRepository.save(ventas);
        return "redirect:/view/pos?exito=true";
    }
}