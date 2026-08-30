//by Jacob Mafla
package com.pillone.pillone.view;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.repository.ComprasRepository;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.ProveedoresRepository;
import com.pillone.pillone.repository.SucursalesRepository;
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

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @Autowired
    private EmpleadosRepository empleadosRepository;

    // LISTAR (misma ruta del submenú, sin choque)
    @GetMapping("/view/compras")
    public String lista(Model model) {
        model.addAttribute("compras", repo.findAll());
        return "compras/compras";
    }

    // FORMULARIO (Crear) -> ruta distinta a la de ComprasController
    @GetMapping("/view/compras/gestion/form")
    public String form(Model model) {
        model.addAttribute("compra", new Compras());
        cargarListas(model);
        return "compras/comprasForm";
    }

    // GUARDAR -> ruta distinta a la de ComprasController
    @PostMapping("/view/compras/gestion/guardar")
    public String save(@Valid @ModelAttribute Compras compra, BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Verifica los campos obligatorios.");
            cargarListas(model);
            return "compras/comprasForm";
        }
        repo.save(compra);
        ra.addFlashAttribute("mensaje", "Orden de compra guardada correctamente");
        return "redirect:/view/compras";
    }

    // EDITAR -> ruta distinta a la de ComprasController
    @GetMapping("/view/compras/gestion/editar/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("compra", repo.findById(id).orElse(new Compras()));
        cargarListas(model);
        return "compras/comprasForm";
    }

    // ELIMINAR -> ruta distinta a la de ComprasController
    @PostMapping("/view/compras/gestion/eliminar/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Orden de compra eliminada");
        return "redirect:/view/compras";
    }

    // Método auxiliar para cargar las listas en los selects
    private void cargarListas(Model model) {
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("proveedores", proveedoresRepository.findAll());
        model.addAttribute("empleados", empleadosRepository.findAll());
    }
}