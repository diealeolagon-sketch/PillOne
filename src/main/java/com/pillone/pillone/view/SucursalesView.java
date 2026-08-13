package com.pillone.pillone.view;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SucursalesView {

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @GetMapping("/view/sucursales")
    public String lista(Model model) {
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        return "sucursales/sucursales";
    }
//acceso a
    @GetMapping("/view/sucursales/form")
    public String form(Model model) {
        model.addAttribute("sucursal", new Sucursales());
        return "sucursales/sucursalesForm";
    }

    @PostMapping("/view/sucursales/save")
    public String save(@ModelAttribute Sucursales sucursales, RedirectAttributes ra) {
        sucursalesRepository.save(sucursales);
        ra.addFlashAttribute("mensaje", "Sucursal registrada exitosamente");
        return "redirect:/view/sucursales";
    }

    @GetMapping("/view/sucursales/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        Sucursales sucursal = sucursalesRepository.findById(id).orElse(null);
        model.addAttribute("sucursal", sucursal);
        return "sucursales/sucursalesForm";
    }

    @PostMapping("/view/sucursales/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sucursalesRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Sucursal eliminada con éxito");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar la sucursal porque tiene empleados registrados. Por favor, reasigne o elimine los empleados primero.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error inesperado al intentar eliminar la sucursal.");
        }
        return "redirect:/view/sucursales";
    }

    @GetMapping("/view/sucursales/detalle/{id}")
    public String verDetalle(@PathVariable long id, Model model) {
        Sucursales sucursal = sucursalesRepository.findById(id).orElse(null);

        // Usa el método que definiste arriba en el Repositorio
        List<Empleados> listaEmpleados = empleadosRepository.findBySucursal_IdSucursal(id);

        model.addAttribute("sucursal", sucursal);
        //se crea el objeto que contiene todos los datos del empleado,el primero es el nombre que usaremos el segundo es el nombre del arreglo o lista
        model.addAttribute("empleadosSucursal", listaEmpleados);
        return "sucursales/sucursalDetalle";
    }
}