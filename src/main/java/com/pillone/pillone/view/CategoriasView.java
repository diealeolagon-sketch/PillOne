package com.pillone.pillone.view;

import com.pillone.pillone.model.Categorias;
import com.pillone.pillone.repository.CategoriasRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoriasView {

    @Autowired
    private CategoriasRepository repo;

    @GetMapping("/view/categorias")
    public String lista(Model model){
        model.addAttribute(
                "categorias",
                repo.findAll()
        );

        return "categorias/categorias";
    }

    @GetMapping("/view/categorias/form")
    public String form(Model model){
        model.addAttribute(
                "categoria",
                new Categorias()
        );

        return "categorias/categoriasForm";
    }

    @PostMapping("/view/categorias/save")
    public String save(
            @Valid @ModelAttribute("categoria") Categorias categoria,
            BindingResult result,
            Model model,
            RedirectAttributes ra
    ){

        if(result.hasErrors()){
            model.addAttribute(
                    "error",
                    "Verifica los campos obligatorios."
            );

            return "categorias/categoriasForm";
        }

        String nombre=
                categoria.getNombre()==null
                        ? ""
                        : categoria.getNombre().trim();

        categoria.setNombre(nombre);

        boolean existe;

        if(categoria.getIdCategoria()==null){

            existe=
                    repo.existsByNombreIgnoreCase(
                            nombre
                    );

        }else{

            existe=
                    repo.existsByNombreIgnoreCaseAndIdCategoriaNot(
                            nombre,
                            categoria.getIdCategoria()
                    );
        }

        if(existe){

            model.addAttribute(
                    "error",
                    "Ya existe una categoría con ese nombre."
            );

            return "categorias/categoriasForm";
        }

        try{

            repo.save(categoria);

            ra.addFlashAttribute(
                    "mensaje",
                    categoria.getIdCategoria()==null
                            ? "Categoría creada correctamente."
                            : "Categoría actualizada correctamente."
            );

        }catch(DataIntegrityViolationException e){

            model.addAttribute(
                    "error",
                    "No fue posible guardar la categoría. Verifica que el nombre no esté repetido."
            );

            return "categorias/categoriasForm";
        }

        return "redirect:/view/categorias";
    }

    @GetMapping("/view/categorias/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes ra
    ){

        Categorias categoria=
                repo.findById(id)
                        .orElse(null);

        if(categoria==null){

            ra.addFlashAttribute(
                    "error",
                    "La categoría no existe."
            );

            return "redirect:/view/categorias";
        }

        model.addAttribute(
                "categoria",
                categoria
        );

        return "categorias/categoriasForm";
    }

    @PostMapping("/view/categorias/delete/{id}")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes ra
    ){

        if(!repo.existsById(id)){

            ra.addFlashAttribute(
                    "error",
                    "La categoría no existe."
            );

            return "redirect:/view/categorias";
        }

        try{

            repo.deleteById(id);
            repo.flush();

            ra.addFlashAttribute(
                    "mensaje",
                    "Categoría eliminada correctamente."
            );

        }catch(DataIntegrityViolationException e){

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar esta categoría porque tiene productos asociados."
            );
        }

        return "redirect:/view/categorias";
    }
}