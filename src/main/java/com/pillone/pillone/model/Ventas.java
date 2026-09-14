package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ventas")
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursales sucursal;

    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Clientes cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleados empleado;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuario;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "aplica_iva", nullable = false)
    private Boolean aplicaIva = true;

    @Column(
            name = "porcentaje_iva",
            precision = 5,
            scale = 2,
            nullable = false
    )
    private BigDecimal porcentajeIva = BigDecimal.ZERO;

    @Column(
            name = "impuesto_iva",
            precision = 12,
            scale = 2,
            nullable = false
    )
    private BigDecimal impuestoIva = BigDecimal.ZERO;

    @Column(
            precision = 12,
            scale = 2,
            nullable = false
    )
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_pago")
    private String metodoPago = "EFECTIVO";

    @Column(name = "estado")
    private String estado = "PAGADA";
}