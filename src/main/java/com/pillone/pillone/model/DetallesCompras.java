package com.pillone.pillone.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_compras")
public class DetallesCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_compra")
    private Integer idDetalleCompra;

    @Column(name = "id_compra", nullable = false)
    private Integer idCompra;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_presentacion", nullable = false)
    private TipoPresentacion tipoPresentacion = TipoPresentacion.CAJA;

    @Column(name = "factor_conversion", nullable = false)
    private Integer factorConversion = 1;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public enum TipoPresentacion {
        CAJA,
        SELLO,
        UNIDAD
    }

    public Integer getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(Integer idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public TipoPresentacion getTipoPresentacion() {
        return tipoPresentacion;
    }

    public void setTipoPresentacion(TipoPresentacion tipoPresentacion) {
        this.tipoPresentacion = tipoPresentacion;
    }

    public Integer getFactorConversion() {
        return factorConversion;
    }

    public void setFactorConversion(Integer factorConversion) {
        this.factorConversion = factorConversion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Transient
    public Integer getCantidadUnidadesBase() {
        if (cantidad == null || factorConversion == null) {
            return 0;
        }

        return cantidad * factorConversion;
    }
}