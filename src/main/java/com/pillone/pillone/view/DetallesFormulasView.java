//by Jacob Mafla
package com.pillone.pillone.view;

import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DetallesFormulasView {

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private FormulasMedicasRepository formulasMedicasRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/view/detalles-formulas")
    public String lista(Model model) {
        model.addAttribute("detallesFormulas", detallesFormulasRepository.findAll());
        return "formulas/detallesFormulas";
    }

    @GetMapping("/view/detalles-formulas/form")
    public String form(Model model) {
        model.addAttribute("detalleFormula", new DetallesFormulas());
        cargarListas(model);
        return "formulas/detallesFormulasForm";
    }

    @PostMapping("/view/detalles-formulas/save")
    public String save(@ModelAttribute DetallesFormulas detalleFormula, RedirectAttributes ra) {
        detallesFormulasRepository.save(detalleFormula);
        ra.addFlashAttribute("mensaje", "Medicamento agregado a la fórmula exitosamente");

        if (detalleFormula.getIdFormula() != null) {
            return "redirect:/view/formulas/detalle/" + detalleFormula.getIdFormula();
        }
        return "redirect:/view/formulas";
    }

    @GetMapping("/view/detalles-formulas/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        DetallesFormulas detalle = detallesFormulasRepository.findById(id).orElse(null);
        model.addAttribute("detalleFormula", detalle);
        cargarListas(model);
        return "formulas/detallesFormulasForm";
    }

    @PostMapping("/view/detalles-formulas/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Long idFormulaRef = null;
        try {
            DetallesFormulas detalle = detallesFormulasRepository.findById(id).orElse(null);
            if (detalle != null) {
                idFormulaRef = detalle.getIdFormula();
            }
            detallesFormulasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Medicamento eliminado de la fórmula correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el detalle.");
        }

        if (idFormulaRef != null) {
            return "redirect:/view/formulas/detalle/" + idFormulaRef;
        }
        return "redirect:/view/formulas";
    }

    private void cargarListas(Model model) {
        model.addAttribute("formulasMedicas", formulasMedicasRepository.findAll());
        model.addAttribute("productos", productosRepository.findAll());
    }
}