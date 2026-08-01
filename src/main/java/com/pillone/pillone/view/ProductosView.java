package com.pillone.pillone.view;

import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductosView
{
    @Autowired
    private ProductosRepository productosRepository;

    // LISTA
    @GetMapping("/view/productos")
    public String lista(Model model)
    {
        model.addAttribute("productos", productosRepository.findAll());

        return "productos/productos";
    }

    // FORMULARIO NUEVO
    @GetMapping("/view/productos/form")
    public String form(Model model)
    {
        model.addAttribute("producto", new Productos());

        return "productos/productosForm";
    }

    // GUARDAR (CREAR / ACTUALIZAR)
    @PostMapping("/view/productos/save")
    public String save(@ModelAttribute Productos productos,
                       RedirectAttributes ra)
    {
        productosRepository.save(productos);

        ra.addFlashAttribute("mensaje",
                "Producto registrado con éxito");

        return "redirect:/view/productos";
    }

    // EDITAR
    @GetMapping("/view/productos/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Productos productos = productosRepository.findById(id).orElse(new Productos());
        model.addAttribute("producto", productos);
        return "productos/productosForm";
    }

    // ELIMINAR
    @PostMapping("/view/productos/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra)
    {
        productosRepository.deleteById(id);

        ra.addFlashAttribute("mensaje",
                "Producto eliminado con éxito");

        return "redirect:/view/productos";
    }

}