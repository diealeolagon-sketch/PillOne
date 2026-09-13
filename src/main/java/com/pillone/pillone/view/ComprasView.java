package com.pillone.pillone.view;

import com.pillone.pillone.model.*;
import com.pillone.pillone.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ComprasView {

    private static final long HORAS_LIMITE_EDICION = 48;

    @Autowired
    private ComprasRepository comprasRepository;

    @Autowired
    private DetallesComprasRepository detallesComprasRepository;

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @GetMapping("/view/compras")
    public String lista(Model model) {

        List<Compras> compras =
                comprasRepository.findAllByOrderByFechaCompraDesc();

        Map<Integer, Proveedores> proveedoresPorId =
                proveedoresRepository.findAll()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Proveedores::getId_proveedor,
                                        p -> p
                                )
                        );

        model.addAttribute(
                "compras",
                compras
        );

        model.addAttribute(
                "proveedoresPorId",
                proveedoresPorId
        );

        model.addAttribute(
                "horasLimiteEdicion",
                HORAS_LIMITE_EDICION
        );

        return "compras/compras";
    }

    @GetMapping("/view/compras/form")
    public String form(
            @RequestParam(required = false) Integer proveedor,
            @RequestParam(required = false) Integer idCompra,
            Model model,
            RedirectAttributes ra
    ) {

        model.addAttribute(
                "proveedores",
                proveedoresRepository.findAll()
        );

        model.addAttribute(
                "productos",
                productosRepository.findAll()
        );

        model.addAttribute(
                "empleados",
                empleadosRepository.findAll()
        );

        model.addAttribute(
                "sucursales",
                sucursalesRepository.findAll()
        );

        model.addAttribute(
                "proveedorSeleccionado",
                proveedor
        );

        if (idCompra != null) {

            Compras compra =
                    comprasRepository
                            .findById(idCompra)
                            .orElse(null);

            if (compra == null) {

                ra.addFlashAttribute(
                        "error",
                        "El pedido no existe."
                );

                return "redirect:/view/compras";
            }

            if (
                    compra.getEstado()
                            != Compras.EstadoCompra.PENDIENTE
            ) {

                ra.addFlashAttribute(
                        "error",
                        "Solo los pedidos pendientes pueden editarse."
                );

                return "redirect:/view/compras";
            }

            if (plazoEdicionVencido(compra)) {

                ra.addFlashAttribute(
                        "error",
                        "El plazo de edición de este pedido ya venció."
                );

                return "redirect:/view/compras";
            }

            List<DetallesCompras> detalles =
                    detallesComprasRepository
                            .findByIdCompra(idCompra);

            BigDecimal porcentajeImpuesto =
                    BigDecimal.ZERO;

            if (
                    compra.getSubtotal() != null
                            &&
                            compra.getSubtotal()
                                    .compareTo(BigDecimal.ZERO)
                                    > 0
                            &&
                            compra.getImpuestos() != null
            ) {

                porcentajeImpuesto =
                        compra.getImpuestos()
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .divide(
                                        compra.getSubtotal(),
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            model.addAttribute(
                    "compraEditar",
                    compra
            );

            model.addAttribute(
                    "detallesEditar",
                    detalles
            );

            model.addAttribute(
                    "proveedorSeleccionado",
                    compra.getIdProveedor()
            );

            model.addAttribute(
                    "numeroPedido",
                    compra.getNumeroFacturaProveedor()
            );

            model.addAttribute(
                    "porcentajeImpuesto",
                    porcentajeImpuesto
            );

        } else {

            model.addAttribute(
                    "numeroPedido",
                    generarNumeroPedido()
            );

            model.addAttribute(
                    "porcentajeImpuesto",
                    BigDecimal.ZERO
            );

            model.addAttribute(
                    "detallesEditar",
                    List.of()
            );
        }

        return "compras/comprasForm";
    }

    @PostMapping("/view/compras/save")
    @Transactional
    public String guardar(
            @RequestParam(required = false) Integer idCompra,
            @RequestParam Integer idProveedor,
            @RequestParam Integer idSucursal,
            @RequestParam Integer idEmpleado,
            @RequestParam String numeroPedido,
            @RequestParam Compras.FormaPago formaPago,
            @RequestParam(defaultValue = "0") BigDecimal porcentajeImpuesto,
            @RequestParam(required = false) List<Long> productoId,
            @RequestParam(required = false) List<String> tipoPresentacion,
            @RequestParam(required = false) List<Integer> cantidad,
            @RequestParam(required = false) List<BigDecimal> precioUnitario,
            RedirectAttributes ra
    ) {

        if (
                productoId == null
                        ||
                        tipoPresentacion == null
                        ||
                        cantidad == null
                        ||
                        precioUnitario == null
                        ||
                        productoId.isEmpty()
        ) {

            ra.addFlashAttribute(
                    "error",
                    "Debe agregar al menos un producto al pedido."
            );

            return redireccionFormulario(
                    idCompra,
                    idProveedor
            );
        }

        if (
                productoId.size()
                        != tipoPresentacion.size()
                        ||
                        productoId.size()
                                != cantidad.size()
                        ||
                        productoId.size()
                                != precioUnitario.size()
        ) {

            ra.addFlashAttribute(
                    "error",
                    "El detalle del pedido es inválido."
            );

            return redireccionFormulario(
                    idCompra,
                    idProveedor
            );
        }

        BigDecimal subtotal =
                BigDecimal.ZERO;

        for (
                int i = 0;
                i < productoId.size();
                i++
        ) {

            Integer cant =
                    cantidad.get(i);

            BigDecimal precio =
                    precioUnitario.get(i);

            if (
                    productoId.get(i) == null
                            ||
                            cant == null
                            ||
                            cant <= 0
                            ||
                            precio == null
                            ||
                            precio.compareTo(
                                    BigDecimal.ZERO
                            ) < 0
            ) {

                ra.addFlashAttribute(
                        "error",
                        "Hay productos, cantidades o precios inválidos."
                );

                return redireccionFormulario(
                        idCompra,
                        idProveedor
                );
            }

            subtotal =
                    subtotal.add(
                            precio.multiply(
                                    BigDecimal.valueOf(
                                            cant
                                    )
                            )
                    );
        }

        BigDecimal impuestos =
                subtotal
                        .multiply(
                                porcentajeImpuesto
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal total =
                subtotal.add(
                        impuestos
                );

        Compras compra;

        if (idCompra != null) {

            compra =
                    comprasRepository
                            .findById(idCompra)
                            .orElse(null);

            if (compra == null) {

                ra.addFlashAttribute(
                        "error",
                        "El pedido no existe."
                );

                return "redirect:/view/compras";
            }

            if (
                    compra.getEstado()
                            != Compras.EstadoCompra.PENDIENTE
            ) {

                ra.addFlashAttribute(
                        "error",
                        "Solo los pedidos pendientes pueden editarse."
                );

                return "redirect:/view/compras";
            }

            if (
                    plazoEdicionVencido(
                            compra
                    )
            ) {

                ra.addFlashAttribute(
                        "error",
                        "El plazo de edición de 48 horas ya venció."
                );

                return "redirect:/view/compras";
            }

            detallesComprasRepository
                    .deleteByIdCompra(
                            idCompra
                    );

        } else {

            compra =
                    new Compras();

            compra.setFechaCompra(
                    LocalDateTime.now()
            );

            compra.setEstado(
                    Compras.EstadoCompra.PENDIENTE
            );
        }

        compra.setIdProveedor(
                idProveedor
        );

        compra.setIdSucursal(
                idSucursal
        );

        compra.setIdEmpleado(
                idEmpleado
        );

        compra.setNumeroFacturaProveedor(
                numeroPedido
        );

        compra.setSubtotal(
                subtotal
        );

        compra.setImpuestos(
                impuestos
        );

        compra.setTotal(
                total
        );

        compra.setFormaPago(
                formaPago
        );

        compra =
                comprasRepository.save(
                        compra
                );

        for (
                int i = 0;
                i < productoId.size();
                i++
        ) {

            Productos producto =
                    productosRepository
                            .findById(
                                    productoId.get(i)
                            )
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Producto no encontrado."
                                            )
                            );

            DetallesCompras.TipoPresentacion presentacion =
                    DetallesCompras.TipoPresentacion
                            .valueOf(
                                    tipoPresentacion.get(i)
                            );

            DetallesCompras detalle =
                    new DetallesCompras();

            detalle.setIdCompra(
                    compra.getIdCompra()
            );

            detalle.setIdProducto(
                    producto.getIdProducto()
            );

            detalle.setTipoPresentacion(
                    presentacion
            );

            detalle.setFactorConversion(
                    calcularFactorConversion(
                            producto,
                            presentacion
                    )
            );

            detalle.setCantidad(
                    cantidad.get(i)
            );

            detalle.setPrecioUnitario(
                    precioUnitario.get(i)
            );

            detalle.setSubtotal(
                    precioUnitario.get(i)
                            .multiply(
                                    BigDecimal.valueOf(
                                            cantidad.get(i)
                                    )
                            )
            );

            detallesComprasRepository
                    .save(
                            detalle
                    );
        }

        ra.addFlashAttribute(
                "mensaje",
                idCompra != null
                        ?
                        "Pedido actualizado correctamente."
                        :
                        "Pedido registrado correctamente."
        );

        return "redirect:/view/compras";
    }

    /*
     * ABRIR PANTALLA DE RECEPCIÓN
     */
    @GetMapping("/view/compras/recibir/{id}")
    public String recibirForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes ra
    ) {

        Compras compra =
                comprasRepository
                        .findById(id)
                        .orElse(null);

        if (compra == null) {

            ra.addFlashAttribute(
                    "error",
                    "El pedido no existe."
            );

            return "redirect:/view/compras";
        }

        if (
                compra.getEstado()
                        != Compras.EstadoCompra.PENDIENTE
                        &&
                        compra.getEstado()
                                != Compras.EstadoCompra.PARCIALMENTE_RECIBIDA
        ) {

            ra.addFlashAttribute(
                    "error",
                    "Este pedido no puede recibirse."
            );

            return "redirect:/view/compras";
        }

        List<DetallesCompras> detalles =
                detallesComprasRepository
                        .findByIdCompra(id);

        Map<Long, Productos> productosPorId =
                productosRepository
                        .findAll()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Productos::getIdProducto,
                                        p -> p
                                )
                        );

        Proveedores proveedor =
                proveedoresRepository
                        .findById(
                                compra.getIdProveedor()
                        )
                        .orElse(null);

        model.addAttribute(
                "compra",
                compra
        );

        model.addAttribute(
                "detalles",
                detalles
        );

        model.addAttribute(
                "productosPorId",
                productosPorId
        );

        model.addAttribute(
                "proveedor",
                proveedor
        );

        return "compras/recepcionCompra";
    }

    /*
     * CONFIRMAR RECEPCIÓN
     */
    @PostMapping("/view/compras/recibir/{id}")
    @Transactional
    public String confirmarRecepcion(
            @PathVariable Integer id,
            @RequestParam List<Integer> detalleId,
            @RequestParam List<Integer> cantidadRecibida,
            @RequestParam List<String> numeroLote,
            @RequestParam(required = false) List<String> fechaFabricacion,
            @RequestParam List<String> fechaVencimiento,
            RedirectAttributes ra
    ) {

        Compras compra =
                comprasRepository
                        .findById(id)
                        .orElse(null);

        if (compra == null) {

            ra.addFlashAttribute(
                    "error",
                    "El pedido no existe."
            );

            return "redirect:/view/compras";
        }

        if (
                compra.getEstado()
                        != Compras.EstadoCompra.PENDIENTE
        ) {

            ra.addFlashAttribute(
                    "error",
                    "Este pedido ya fue procesado."
            );

            return "redirect:/view/compras";
        }

        if (
                detalleId.size()
                        != cantidadRecibida.size()
                        ||
                        detalleId.size()
                                != numeroLote.size()
                        ||
                        detalleId.size()
                                != fechaVencimiento.size()
        ) {

            ra.addFlashAttribute(
                    "error",
                    "Los datos de recepción son inválidos."
            );

            return "redirect:/view/compras/recibir/"
                    + id;
        }

        List<DetallesCompras> detalles =
                detallesComprasRepository
                        .findByIdCompra(id);

        Map<Integer, DetallesCompras> detallesPorId =
                detalles
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        DetallesCompras::getIdDetalleCompra,
                                        d -> d
                                )
                        );

        int totalLineas =
                detalles.size();

        int lineasCompletas = 0;

        for (
                int i = 0;
                i < detalleId.size();
                i++
        ) {

            DetallesCompras detalle =
                    detallesPorId.get(
                            detalleId.get(i)
                    );

            if (detalle == null) {

                throw new RuntimeException(
                        "Detalle de compra inválido."
                );
            }

            Integer recibido =
                    cantidadRecibida.get(i);

            if (
                    recibido == null
                            ||
                            recibido < 0
                            ||
                            recibido > detalle.getCantidad()
            ) {

                throw new RuntimeException(
                        "La cantidad recibida no puede superar la cantidad pedida."
                );
            }

            if (recibido == 0) {

                continue;
            }

            String loteNumero =
                    numeroLote.get(i);

            String vencimiento =
                    fechaVencimiento.get(i);

            if (
                    loteNumero == null
                            ||
                            loteNumero.isBlank()
            ) {

                throw new RuntimeException(
                        "Debe indicar el número de lote."
                );
            }

            if (
                    vencimiento == null
                            ||
                            vencimiento.isBlank()
            ) {

                throw new RuntimeException(
                        "Debe indicar la fecha de vencimiento."
                );
            }

            Productos producto =
                    productosRepository
                            .findById(
                                    detalle.getIdProducto()
                            )
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Producto no encontrado."
                                            )
                            );

            int factor =
                    detalle.getFactorConversion() == null
                            ?
                            1
                            :
                            detalle.getFactorConversion();

            int unidadesRecibidas =
                    recibido
                            *
                            factor;

            Lotes lote =
                    new Lotes();

            lote.setIdProducto(
                    producto.getIdProducto()
            );

            lote.setNumeroLote(
                    loteNumero.trim()
            );

            if (
                    fechaFabricacion != null
                            &&
                            i < fechaFabricacion.size()
                            &&
                            fechaFabricacion.get(i) != null
                            &&
                            !fechaFabricacion
                                    .get(i)
                                    .isBlank()
            ) {

                lote.setFechaFabricacion(
                        LocalDate.parse(
                                fechaFabricacion.get(i)
                        )
                );
            }

            lote.setFechaVencimiento(
                    LocalDate.parse(
                            vencimiento
                    )
            );

            lote.setCantidadInicial(
                    unidadesRecibidas
            );

            lote.setCantidadActual(
                    unidadesRecibidas
            );

            lote.setEstado(
                    "DISPONIBLE"
            );

            lotesRepository.save(
                    lote
            );

            int stockActual =
                    producto.getStockTotal() == null
                            ?
                            0
                            :
                            producto.getStockTotal();

            producto.setStockTotal(
                    stockActual
                            +
                            unidadesRecibidas
            );

            productosRepository.save(
                    producto
            );

            if (
                    recibido.equals(
                            detalle.getCantidad()
                    )
            ) {

                lineasCompletas++;
            }
        }

        /*
         * En esta primera versión:
         *
         * - si todo llegó completo → RECIBIDA
         * - si alguna línea llegó parcial o no llegó → PARCIALMENTE_RECIBIDA
         */

        if (
                lineasCompletas
                        ==
                        totalLineas
        ) {

            compra.setEstado(
                    Compras.EstadoCompra.RECIBIDA
            );

        } else {

            compra.setEstado(
                    Compras.EstadoCompra.PARCIALMENTE_RECIBIDA
            );
        }

        comprasRepository.save(
                compra
        );

        ra.addFlashAttribute(
                "mensaje",
                compra.getEstado()
                        ==
                        Compras.EstadoCompra.RECIBIDA
                        ?
                        "Pedido recibido correctamente. Los lotes y el inventario fueron actualizados."
                        :
                        "Recepción parcial registrada. El inventario fue actualizado con la mercancía recibida."
        );

        return "redirect:/view/compras";
    }

    @PostMapping("/view/compras/cancelar/{id}")
    public String cancelar(
            @PathVariable Integer id,
            RedirectAttributes ra
    ) {

        Compras compra =
                comprasRepository
                        .findById(id)
                        .orElse(null);

        if (compra == null) {

            ra.addFlashAttribute(
                    "error",
                    "El pedido no existe."
            );

            return "redirect:/view/compras";
        }

        if (
                compra.getEstado()
                        == Compras.EstadoCompra.RECIBIDA
                        ||
                        compra.getEstado()
                                == Compras.EstadoCompra.PARCIALMENTE_RECIBIDA
        ) {

            ra.addFlashAttribute(
                    "error",
                    "Una compra que ya tiene mercancía recibida no puede cancelarse directamente."
            );

            return "redirect:/view/compras";
        }

        compra.setEstado(
                Compras.EstadoCompra.CANCELADA
        );

        comprasRepository.save(
                compra
        );

        ra.addFlashAttribute(
                "mensaje",
                "Pedido cancelado correctamente."
        );

        return "redirect:/view/compras";
    }

    @PostMapping("/view/compras/reactivar/{id}")
    public String reactivar(
            @PathVariable Integer id,
            RedirectAttributes ra
    ) {

        Compras compra =
                comprasRepository
                        .findById(id)
                        .orElse(null);

        if (
                compra == null
                        ||
                        compra.getEstado()
                                != Compras.EstadoCompra.CANCELADA
        ) {

            ra.addFlashAttribute(
                    "error",
                    "El pedido no puede reactivarse."
            );

            return "redirect:/view/compras";
        }

        compra.setEstado(
                Compras.EstadoCompra.PENDIENTE
        );

        comprasRepository.save(
                compra
        );

        ra.addFlashAttribute(
                "mensaje",
                "Pedido reactivado correctamente."
        );

        return "redirect:/view/compras";
    }

    @PostMapping("/view/compras/delete/{id}")
    @Transactional
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes ra
    ) {

        Compras compra =
                comprasRepository
                        .findById(id)
                        .orElse(null);

        if (compra == null) {

            ra.addFlashAttribute(
                    "error",
                    "El pedido no existe."
            );

            return "redirect:/view/compras";
        }

        if (
                compra.getEstado()
                        == Compras.EstadoCompra.RECIBIDA
                        ||
                        compra.getEstado()
                                == Compras.EstadoCompra.PARCIALMENTE_RECIBIDA
        ) {

            ra.addFlashAttribute(
                    "error",
                    "No puede eliminar una compra que ya afectó el inventario."
            );

            return "redirect:/view/compras";
        }

        detallesComprasRepository
                .deleteByIdCompra(id);

        comprasRepository
                .deleteById(id);

        ra.addFlashAttribute(
                "mensaje",
                "Pedido eliminado correctamente."
        );

        return "redirect:/view/compras";
    }

    private int calcularFactorConversion(
            Productos producto,
            DetallesCompras.TipoPresentacion presentacion
    ) {

        if (
                presentacion
                        ==
                        DetallesCompras.TipoPresentacion.UNIDAD
        ) {

            return 1;
        }

        if (
                presentacion
                        ==
                        DetallesCompras.TipoPresentacion.SELLO
        ) {

            return producto.getUnidadesPorSello() != null
                    &&
                    producto.getUnidadesPorSello() > 0
                    ?
                    producto.getUnidadesPorSello()
                    :
                    1;
        }

        if (
                producto.getUnidadesPorEmpaque() != null
                        &&
                        producto.getUnidadesPorEmpaque() > 0
        ) {

            return producto.getUnidadesPorEmpaque();
        }

        int sellos =
                producto.getSellosPorCaja() == null
                        ?
                        1
                        :
                        producto.getSellosPorCaja();

        int unidadesSello =
                producto.getUnidadesPorSello() == null
                        ?
                        1
                        :
                        producto.getUnidadesPorSello();

        return Math.max(
                1,
                sellos * unidadesSello
        );
    }

    private boolean plazoEdicionVencido(
            Compras compra
    ) {

        if (
                compra.getFechaCompra()
                        == null
        ) {

            return true;
        }

        return !LocalDateTime.now()
                .isBefore(
                        compra.getFechaCompra()
                                .plusHours(
                                        HORAS_LIMITE_EDICION
                                )
                );
    }

    private String redireccionFormulario(
            Integer idCompra,
            Integer proveedor
    ) {

        if (idCompra != null) {

            return "redirect:/view/compras/form?idCompra="
                    + idCompra;
        }

        if (proveedor != null) {

            return "redirect:/view/compras/form?proveedor="
                    + proveedor;
        }

        return "redirect:/view/compras/form";
    }

    private String generarNumeroPedido() {

        return "PED-"
                +
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter
                                        .ofPattern(
                                                "yyyyMMddHHmmss"
                                        )
                        );
    }
}