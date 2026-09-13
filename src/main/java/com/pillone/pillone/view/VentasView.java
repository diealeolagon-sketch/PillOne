package com.pillone.pillone.view;

import com.pillone.pillone.model.Ventas;
import com.pillone.pillone.model.DetallesVentas;
import com.pillone.pillone.repository.VentasRepository;
import com.pillone.pillone.repository.DetallesVentasRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import com.pillone.pillone.repository.ClientesRepository;
import com.pillone.pillone.repository.EmpleadosRepository;
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
public class VentasView {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @GetMapping("/view/ventas")
    public String lista(Model model) {
        model.addAttribute("ventas", ventasRepository.findAll());
        return "ventas/ventas";
    }

    @GetMapping("/view/ventas/form")
    public String form(Model model) {
        model.addAttribute("venta", new Ventas());
        cargarListas(model); // Crucial para que lleguen sucursales, clientes y empleados
        return "ventas/ventasForm";
    }

    @PostMapping("/view/ventas/save")
    public String save(@ModelAttribute Ventas ventas, RedirectAttributes ra) {
        ventasRepository.save(ventas);
        ra.addFlashAttribute("mensaje", "Venta registrada exitosamente");
        return "redirect:/view/ventas";
    }

    @GetMapping("/view/ventas/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        Ventas venta = ventasRepository.findById(id).orElse(null);
        model.addAttribute("venta", venta);
        cargarListas(model);
        return "ventas/ventasForm";
    }

    @PostMapping("/view/ventas/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ventasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Venta eliminada con éxito");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar la venta porque tiene registros asociados (detalles de venta).");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error inesperado al intentar eliminar la venta.");
        }
        return "redirect:/view/ventas";
    }

    @GetMapping("/view/ventas/detalle/{id}")
    public String verDetalle(@PathVariable long id, Model model) {
        Ventas venta = ventasRepository.findById(id).orElse(null);
        List<DetallesVentas> listaDetalles = detallesVentasRepository.findByVenta_IdVenta(id);

        model.addAttribute("venta", venta);
        model.addAttribute("detallesVenta", listaDetalles);
        return "ventas/ventaDetalle";
    }

    private void cargarListas(Model model) {
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("clientes", clientesRepository.findAll());
        model.addAttribute("empleados", empleadosRepository.findAll());
    }
}