//by Jacob Mafla
package com.pillone.pillone.controller;

import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/view/detalles-formulas-mgmt")
public class DetallesFormulasController {

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private FormulasMedicasRepository formulasMedicasRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping
    public String lista(Model model) {

        model.addAttribute(
                "detallesFormulas",
                detallesFormulasRepository.findAll()
        );

        return "formulas/detallesFormulas";
    }

    @GetMapping("/form")
    public String form(Model model) {

        model.addAttribute(
                "detalleFormula",
                new DetallesFormulas()
        );

        cargarListas(model);

        return "formulas/detallesFormulasForm";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute DetallesFormulas detalleFormula,
            RedirectAttributes ra
    ) {

        detallesFormulasRepository.save(
                detalleFormula
        );

        ra.addFlashAttribute(
                "mensaje",
                "Medicamento de la fórmula guardado exitosamente."
        );

        if (detalleFormula.getIdFormula() != null) {

            return "redirect:/view/formulas/detalle/"
                    + detalleFormula.getIdFormula();
        }

        return "redirect:/view/formulas";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {

        DetallesFormulas detalle =
                detallesFormulasRepository
                        .findById(id)
                        .orElse(null);

        if (detalle == null) {

            ra.addFlashAttribute(
                    "error",
                    "El detalle de fórmula no existe."
            );

            return "redirect:/view/formulas";
        }

        model.addAttribute(
                "detalleFormula",
                detalle
        );

        cargarListas(model);

        return "formulas/detallesFormulasForm";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {

        Long idFormulaRef = null;

        try {

            DetallesFormulas detalle =
                    detallesFormulasRepository
                            .findById(id)
                            .orElse(null);

            if (detalle != null) {

                idFormulaRef =
                        detalle.getIdFormula();
            }

            detallesFormulasRepository
                    .deleteById(id);

            ra.addFlashAttribute(
                    "mensaje",
                    "Medicamento eliminado de la fórmula correctamente."
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "Ocurrió un error al intentar eliminar el detalle."
            );
        }

        if (idFormulaRef != null) {

            return "redirect:/view/formulas/detalle/"
                    + idFormulaRef;
        }

        return "redirect:/view/formulas";
    }

    private void cargarListas(Model model) {

        model.addAttribute(
                "formulasMedicas",
                formulasMedicasRepository.findAll()
        );

        model.addAttribute(
                "productos",
                productosRepository.findAll()
        );
    }
}