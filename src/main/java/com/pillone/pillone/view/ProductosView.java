package com.pillone.pillone.view;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.CategoriasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import com.pillone.pillone.repository.ProveedoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductosView {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private CategoriasRepository categoriasRepository;

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    // LISTA
    @GetMapping("/view/productos")
    public String lista(Model model) {
        model.addAttribute("productos", productosRepository.findAll());
        return "productos/productos";
    }

    // FORMULARIO CREAR
    @GetMapping("/view/productos/form")
    public String form(Model model) {
        Productos producto = new Productos();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriasRepository.findAll());
        model.addAttribute("proveedores", proveedoresRepository.findAll());
        return "productos/productosForm";
    }

    // GUARDAR (CREAR O ACTUALIZAR)
    @PostMapping("/view/productos/save")
    public String save(
            @Valid @ModelAttribute Productos producto,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("error", "Hay campos obligatorios vacíos o incorrectos.");
            model.addAttribute("categorias", categoriasRepository.findAll());
            model.addAttribute("proveedores", proveedoresRepository.findAll());
            return "productos/productosForm";
        }

        // Validación opcional si deseas verificar código interno único al crear
        if (producto.getIdProducto() == null && producto.getCodigoInterno() != null) {
            boolean existe = productosRepository.findAll().stream()
                    .anyMatch(p -> p.getCodigoInterno().equals(producto.getCodigoInterno()));
            if (existe) {
                model.addAttribute("error", "Ese código interno ya está registrado.");
                model.addAttribute("categorias", categoriasRepository.findAll());
                model.addAttribute("proveedores", proveedoresRepository.findAll());
                return "productos/productosForm";
            }
        }

        productosRepository.save(producto);
        ra.addFlashAttribute("mensaje", "Producto guardado con éxito");
        return "redirect:/view/productos";
    }

    // EDITAR
    @GetMapping("/view/productos/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Productos producto = productosRepository.findById(id).orElse(null);
        if (producto == null) return "redirect:/view/productos";

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriasRepository.findAll());
        model.addAttribute("proveedores", proveedoresRepository.findAll());
        return "productos/productosForm";
    }

    // ELIMINAR
    @PostMapping("/view/productos/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        productosRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Producto eliminado");
        return "redirect:/view/productos";
    }
}