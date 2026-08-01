package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;

    @NotNull(message = "El ID de la venta es obligatorio")
    @Column(name = "id_venta", nullable = false)
    private Long idVenta;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @NotNull(message = "El ID del lote es obligatorio")
    @Column(name = "id_lote", nullable = false)
    private Long idLote;

    @NotNull(message = "La cantidad es obligatoria")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;
}