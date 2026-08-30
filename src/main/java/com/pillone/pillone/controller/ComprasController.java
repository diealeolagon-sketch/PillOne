package com.pillone.pillone.controller;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.repository.ComprasRepository;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.ProveedoresRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/view/compras")
public class ComprasController
{
    @Autowired
    private ComprasRepository comprasRepository;
    @Autowired
    private SucursalesRepository sucursalesRepository;
    @Autowired
    private ProveedoresRepository proveedoresRepository;
    @Autowired
    private EmpleadosRepository empleadosRepository;

    // Vista del formulario para NUEVA compra
    @GetMapping("/form")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("compra", new Compras());
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("proveedores", proveedoresRepository.findAll());
        model.addAttribute("empleados", empleadosRepository.findAll());
        return "compras/comprasForm"; // Asegúrate de que coincida con la ruta de tu archivo HTML
    }

    // Vista del formulario para EDITAR compra existente
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Compras compra = comprasRepository.findById(id).orElse(new Compras());
        model.addAttribute("compra", compra);
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("proveedores", proveedoresRepository.findAll());
        model.addAttribute("empleados", empleadosRepository.findAll());
        return "compras/comprasForm";
    }

    // Guardar los datos enviados desde el formulario web
    @PostMapping("/save")
    public String guardarCompra(@ModelAttribute Compras compra) {
        comprasRepository.save(compra);
        return "redirect:/view/compras";
    }
}