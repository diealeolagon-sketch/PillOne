package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "codigo_interno", unique = true, nullable = false, length = 30)
    private String codigoInterno;

    @Column(name = "codigo_barras", unique = true, nullable = false, length = 50)
    private String codigoBarras;

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

    @Column(name = "registro_invima", nullable = false, length = 50)
    private String registroInvima;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categorias categoria;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedores proveedor;

    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_total")
    private Integer stockTotal = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 10;

    @Column(name = "ubicacion_estante", length = 50)
    private String ubicacionEstante;

    @Column(name = "requiere_formula")
    private Boolean requiereFormula = false;

    @Column(name = "es_venta_libre")
    private Boolean esVentaLibre = true;

    @Column(name = "es_controlado")
    private Boolean esControlado = false;

    @Column(name = "requiere_refrigeracion")
    private Boolean requiereRefrigeracion = false;

    @Column(name = "restricciones_venta", length = 200)
    private String restriccionesVenta;

    @Column(name = "estado")
    private String estado = "ACTIVO";
}