package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El código interno es obligatorio")
    @Column(name = "codigo_interno", nullable = false, unique = true, length = 50)
    private String codigoInterno;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @Column(name = "nombre_generico", length = 150)
    private String nombreGenerico;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "presentacion", length = 100)
    private String presentacion;

    @Column(name = "concentracion", length = 50)
    private String concentracion;

    @Column(name = "laboratorio", length = 100)
    private String laboratorio;

    @Column(name = "registro_invima", length = 50)
    private String registroInvima;

    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "id_proveedor")
    private Long idProveedor;

    @NotNull(message = "El precio de compra es obligatorio")
    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_total")
    private Integer stockTotal;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "ubicacion_estante", length = 50)
    private String ubicacionEstante;

    @Column(name = "requiere_formula")
    private Boolean requiereFormula;

    @Column(name = "es_venta_libre")
    private Boolean esVentaLibre;

    @Column(name = "es_controlado")
    private Boolean esControlado;

    @Column(name = "requiere_refrigeracion")
    private Boolean requiereRefrigeracion;

    @Column(name = "restricciones_venta", length = 255)
    private String restriccionesVenta;

    @Column(name = "estado", length = 50)
    private String estado;
}