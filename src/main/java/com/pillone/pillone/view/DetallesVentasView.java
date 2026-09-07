package com.pillone.pillone.view;

import com.pillone.pillone.model.DetallesVentas;
import com.pillone.pillone.repository.DetallesVentasRepository;
import com.pillone.pillone.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DetallesVentasView {

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @GetMapping("/view/detalles-ventas")
    public String lista(Model model) {
        model.addAttribute("detallesVentas", detallesVentasRepository.findAll());
        return "ventas/detallesVentas";
    }

    @GetMapping("/view/detalles-ventas/form")
    public String form(Model model) {
        model.addAttribute("detalleVenta", new DetallesVentas());
        model.addAttribute("ventas", ventasRepository.findAll());
        return "ventas/detallesVentasForm";
    }

    @PostMapping("/view/detalles-ventas/save")
    public String save(@ModelAttribute DetallesVentas detalleVenta, RedirectAttributes ra) {
        // Cálculo automático del subtotal por línea (cantidad * precio unitario)
        if (detalleVenta.getCantidad() != null && detalleVenta.getPrecioUnitario() != null) {
            detalleVenta.setSubtotal(detalleVenta.getPrecioUnitario().multiply(java.math.BigDecimal.valueOf(detalleVenta.getCantidad())));
        }

        detallesVentasRepository.save(detalleVenta);
        ra.addFlashAttribute("mensaje", "Línea de detalle guardada exitosamente");
        return "redirect:/view/ventas/detalle/" + detalleVenta.getVenta().getIdVenta();
    }

    @GetMapping("/view/detalles-ventas/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        DetallesVentas detalle = detallesVentasRepository.findById(id).orElse(null);
        model.addAttribute("detalleVenta", detalle);
        model.addAttribute("ventas", ventasRepository.findAll());
        return "ventas/detallesVentasForm";
    }

    @PostMapping("/view/detalles-ventas/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Long idVentaRef = null;
        try {
            DetallesVentas detalle = detallesVentasRepository.findById(id).orElse(null);
            if (detalle != null && detalle.getVenta() != null) {
                idVentaRef = detalle.getVenta().getIdVenta();
            }
            detallesVentasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Línea eliminada del detalle correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el detalle de venta.");
        }

        if (idVentaRef != null) {
            return "redirect:/view/ventas/detalle/" + idVentaRef;
        }
        return "redirect:/view/ventas";
    }
}