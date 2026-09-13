package com.pillone.pillone.view;

import com.pillone.pillone.model.Compras;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.model.Proveedores;
import com.pillone.pillone.repository.ComprasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import com.pillone.pillone.repository.ProveedoresRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
public class ProveedoresView {

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private ComprasRepository comprasRepository;

    @GetMapping("/view/proveedores")
    public String lista(Model model) {
        model.addAttribute(
                "proveedores",
                proveedoresRepository.findAll()
        );

        return "proveedores/proveedores";
    }

    @GetMapping("/view/proveedores/form")
    public String form(Model model) {
        Proveedores proveedor = new Proveedores();

        proveedor.setEstado(
                Proveedores.EstadoProveedor.ACTIVO
        );

        model.addAttribute(
                "proveedor",
                proveedor
        );

        return "proveedores/proveedoresForm";
    }

    @PostMapping("/view/proveedores/save")
    public String save(
            @Valid @ModelAttribute("proveedor") Proveedores proveedor,
            BindingResult result,
            Model model,
            RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            model.addAttribute(
                    "error",
                    "Verifique los campos obligatorios."
            );

            return "proveedores/proveedoresForm";
        }

        boolean nuevo =
                proveedor.getId_proveedor() == null;

        proveedoresRepository.save(
                proveedor
        );

        ra.addFlashAttribute(
                "mensaje",
                nuevo
                        ? "Proveedor registrado correctamente."
                        : "Proveedor guardado correctamente."
        );

        return "redirect:/view/proveedores";
    }

    /*
     * FICHA DEL PROVEEDOR.
     * Esta pantalla es únicamente de consulta.
     *
     * Desde aquí:
     * - se ven los datos del proveedor
     * - se ven los productos asociados
     * - se ve el historial de pedidos
     *
     * NO se editan pedidos desde esta pantalla.
     */
    @GetMapping("/view/proveedores/ver/{id}")
    public String ver(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes ra
    ) {
        Proveedores proveedor =
                proveedoresRepository
                        .findById(id)
                        .orElse(null);

        if (proveedor == null) {
            ra.addFlashAttribute(
                    "error",
                    "El proveedor no existe."
            );

            return "redirect:/view/proveedores";
        }

        List<Productos> productos =
                productosRepository
                        .findAll()
                        .stream()
                        .filter(
                                producto ->
                                        Objects.equals(
                                                producto.getIdProveedor(),
                                                id
                                        )
                        )
                        .toList();

        List<Compras> compras =
                comprasRepository
                        .findByIdProveedorOrderByFechaCompraDesc(
                                id
                        );

        model.addAttribute(
                "proveedor",
                proveedor
        );

        model.addAttribute(
                "productosProveedor",
                productos
        );

        model.addAttribute(
                "comprasProveedor",
                compras
        );

        return "proveedores/proveedorDetalle";
    }

    /*
     * Ya no usamos la edición del proveedor
     * desde el directorio.
     *
     * La ruta se deja únicamente por compatibilidad
     * por si algún enlace antiguo intenta entrar.
     * Redirige a la ficha de consulta.
     */
    @GetMapping("/view/proveedores/edit/{id}")
    public String edit(
            @PathVariable Integer id
    ) {
        return "redirect:/view/proveedores/ver/" + id;
    }

    /*
     * En lugar de eliminar proveedores con historial,
     * manejamos ACTIVO / INACTIVO.
     */
    @PostMapping("/view/proveedores/cambiar-estado/{id}")
    public String cambiarEstado(
            @PathVariable Integer id,
            RedirectAttributes ra
    ) {
        Proveedores proveedor =
                proveedoresRepository
                        .findById(id)
                        .orElse(null);

        if (proveedor == null) {
            ra.addFlashAttribute(
                    "error",
                    "El proveedor no existe."
            );

            return "redirect:/view/proveedores";
        }

        if (
                proveedor.getEstado()
                        == Proveedores.EstadoProveedor.ACTIVO
        ) {
            proveedor.setEstado(
                    Proveedores.EstadoProveedor.INACTIVO
            );

            ra.addFlashAttribute(
                    "mensaje",
                    "Proveedor inactivado correctamente."
            );
        } else {
            proveedor.setEstado(
                    Proveedores.EstadoProveedor.ACTIVO
            );

            ra.addFlashAttribute(
                    "mensaje",
                    "Proveedor activado correctamente."
            );
        }

        proveedoresRepository.save(
                proveedor
        );

        return "redirect:/view/proveedores";
    }
}