package com.pillone.pillone.view;

import com.pillone.pillone.model.Clientes;
import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.ClientesRepository;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ClienteDetalleView {

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private FormulasMedicasRepository formulasMedicasRepository;

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/view/clientes/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes ra) {

        Clientes cliente = clientesRepository.findById(id).orElse(null);

        if (cliente == null) {
            ra.addFlashAttribute("error", "El cliente no existe.");
            return "redirect:/view/clientes";
        }

        List<FormulasMedicas> formulas = formulasMedicasRepository.findByIdCliente(id);

        formulas.sort(
                Comparator.comparing(
                        FormulasMedicas::getFechaExpedicion,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        Map<Long, List<DetallesFormulas>> detallesPorFormula = new LinkedHashMap<>();

        for (FormulasMedicas formula : formulas) {
            detallesPorFormula.put(
                    formula.getIdFormula(),
                    detallesFormulasRepository.findByIdFormula(formula.getIdFormula())
            );
        }

        Map<Long, Productos> productosPorId = productosRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Productos::getIdProducto,
                        producto -> producto,
                        (primero, segundo) -> primero
                ));

        model.addAttribute("cliente", cliente);
        model.addAttribute("formulas", formulas);
        model.addAttribute("detallesPorFormula", detallesPorFormula);
        model.addAttribute("productosPorId", productosPorId);

        return "clientes/clienteDetalle";
    }
}