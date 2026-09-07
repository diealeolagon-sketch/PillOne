//by Jacob Mafla
package com.pillone.pillone.controller;

import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.ClientesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/view/formulas-avanzadas") // Ruta completamente independiente
public class FormulasMedicasController {

    @Autowired
    private FormulasMedicasRepository formulasRepository;

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("formulas", formulasRepository.findAll());
        return "formulas/formulasMedicas";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("formulaMedica", new FormulasMedicas());
        cargarListas(model);
        return "formulas/formulasMedicasForm";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FormulasMedicas formulaMedica,
                       @RequestParam(required = false) List<Long> idProductos,
                       @RequestParam(required = false) List<String> dosisList,
                       @RequestParam(required = false) List<String> frecuenciaList,
                       @RequestParam(required = false) List<String> duracionTratamientoList,
                       RedirectAttributes ra) {
        try {
            if (formulaMedica.getFechaExpedicion() == null) {
                formulaMedica.setFechaExpedicion(LocalDate.now());
            }
            FormulasMedicas formulaGuardada = formulasRepository.save(formulaMedica);

            if (idProductos != null && !idProductos.isEmpty()) {
                for (int i = 0; i < idProductos.size(); i++) {
                    DetallesFormulas detalle = new DetallesFormulas();
                    detalle.setIdFormula(formulaGuardada.getIdFormula());
                    detalle.setIdProducto(idProductos.get(i));
                    detalle.setDosis(dosisList != null && dosisList.size() > i ? dosisList.get(i) : "");
                    detalle.setFrecuencia(frecuenciaList != null && frecuenciaList.size() > i ? frecuenciaList.get(i) : "");
                    detalle.setDuracionTratamiento(duracionTratamientoList != null && duracionTratamientoList.size() > i ? duracionTratamientoList.get(i) : "");
                    detallesFormulasRepository.save(detalle);
                }
            }

            ra.addFlashAttribute("mensaje", "Fórmula médica registrada exitosamente.");
            return "redirect:/view/formulas/cliente/" + formulaMedica.getIdCliente();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar la fórmula médica.");
            return "redirect:/view/formulas-avanzadas/form";
        }
    }

    private void cargarListas(Model model) {
        model.addAttribute("clientes", clientesRepository.findAll());
        model.addAttribute("productos", productosRepository.findAll());
    }
}