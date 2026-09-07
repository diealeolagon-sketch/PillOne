//by Jacob Mafla
package com.pillone.pillone.view;

import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.ClientesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FormulasMedicasView {

    @Autowired
    private FormulasMedicasRepository formulasMedicasRepository;

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/view/formulas")
    public String lista(Model model) {
        model.addAttribute("formulas", formulasMedicasRepository.findAll());
        return "formulas/formulasMedicas";
    }

    @GetMapping("/view/formulas/form")
    public String form(Model model) {
        model.addAttribute("formulaMedica", new FormulasMedicas());
        cargarListas(model);
        return "formulas/formulasMedicasForm";
    }

    @PostMapping("/view/formulas/save")
    public String save(@ModelAttribute FormulasMedicas formulaMedica, RedirectAttributes ra) {
        if (formulaMedica.getFechaExpedicion() == null) {
            formulaMedica.setFechaExpedicion(LocalDate.now());
        }
        formulasMedicasRepository.save(formulaMedica);
        ra.addFlashAttribute("mensaje", "Fórmula médica registrada exitosamente");
        return "redirect:/view/formulas";
    }

    @GetMapping("/view/formulas/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        FormulasMedicas formula = formulasMedicasRepository.findById(id).orElse(null);
        model.addAttribute("formulaMedica", formula);
        cargarListas(model);
        return "formulas/formulasMedicasForm";
    }

    @PostMapping("/view/formulas/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            formulasMedicasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Fórmula médica eliminada con éxito");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la fórmula porque tiene medicamentos asociados.");
        }
        return "redirect:/view/formulas";
    }

    @GetMapping("/view/formulas/cliente/{idCliente}")
    public String historialCliente(@PathVariable Long idCliente, Model model) {
        List<FormulasMedicas> formulas = formulasMedicasRepository.findByIdCliente(idCliente);

        LocalDate hoy = LocalDate.now();
        for (FormulasMedicas f : formulas) {
            int diasVigencia = (f.getVigenciaDias() != null) ? f.getVigenciaDias() : 30;
            LocalDate fechaVencimiento = f.getFechaExpedicion().plusDays(diasVigencia);

            if (hoy.isAfter(fechaVencimiento)) {
                f.setEstado("VENCIDA");
            } else {
                f.setEstado("VIGENTE");
            }
        }

        model.addAttribute("cliente", clientesRepository.findById(idCliente).orElse(null));
        model.addAttribute("formulas", formulas);
        return "formulas/historialCliente";
    }

    @GetMapping("/view/formulas/detalle/{id}")
    public String verDetalle(@PathVariable long id, Model model) {
        FormulasMedicas formula = formulasMedicasRepository.findById(id).orElse(null);
        List<DetallesFormulas> listaDetalles = detallesFormulasRepository.findByIdFormula(id);

        model.addAttribute("formulaMedica", formula);
        model.addAttribute("detallesFormula", listaDetalles);
        return "formulas/formulaDetalle";
    }

    private void cargarListas(Model model) {
        model.addAttribute("clientes", clientesRepository.findAll());
        model.addAttribute("productos", productosRepository.findAll());
    }
}