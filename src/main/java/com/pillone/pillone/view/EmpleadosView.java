package com.pillone.pillone.view;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmpleadosView {

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @Autowired
    private SucursalesRepository sucursalesRepository; // Necesario para el select

    // LISTA
    @GetMapping("/view/empleados")
    public String lista(Model model) {
        model.addAttribute("empleados", empleadosRepository.findAll());
        return "empleados/empleados";
    }

    // FORMULARIO CREAR
    @GetMapping("/view/empleados/form")
    public String form(Model model) {
        model.addAttribute("empleado", new Empleados());
        model.addAttribute("sucursales", sucursalesRepository.findAll()); // Pasamos sucursales
        return "empleados/empleadosForm";
    }

    // GUARDAR O ACTUALIZAR
    @PostMapping("/view/empleados/save")
    public String save(@ModelAttribute Empleados empleados, RedirectAttributes ra) {
        empleadosRepository.save(empleados);
        ra.addFlashAttribute("mensaje", "Empleado registrado con éxito");
        return "redirect:/view/empleados";
    }

    // EDITAR
    @GetMapping("/view/empleados/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Empleados empleado = empleadosRepository.findById(id).orElse(null);
        model.addAttribute("empleado", empleado);
        model.addAttribute("sucursales", sucursalesRepository.findAll()); // Pasamos sucursales
        return "empleados/empleadosForm";
    }

    // ELIMINAR
    @PostMapping("/view/empleados/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        empleadosRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Empleado eliminado con éxito");
        return "redirect:/view/empleados";
    }
}