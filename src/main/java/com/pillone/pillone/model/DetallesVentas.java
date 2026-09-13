package com.pillone.pillone.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_ventas")
public class DetallesVentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Ventas venta;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(name = "id_formula")
    private Long idFormula;

    @Column(name = "id_lote", nullable = false)
    private Long idLote;

    @Column(name = "tipo_venta", nullable = false, length = 20)
    private String tipoVenta;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "unidades_descontadas", nullable = false)
    private Integer unidadesDescontadas;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public DetallesVentas(){}

    public Long getIdDetalleVenta(){return idDetalleVenta;}
    public void setIdDetalleVenta(Long idDetalleVenta){this.idDetalleVenta=idDetalleVenta;}

    public Ventas getVenta(){return venta;}
    public void setVenta(Ventas venta){this.venta=venta;}

    public Long getIdProducto(){return idProducto;}
    public void setIdProducto(Long idProducto){this.idProducto=idProducto;}

    public Long getIdFormula(){return idFormula;}
    public void setIdFormula(Long idFormula){this.idFormula=idFormula;}

    public Long getIdLote(){return idLote;}
    public void setIdLote(Long idLote){this.idLote=idLote;}

    public String getTipoVenta(){return tipoVenta;}
    public void setTipoVenta(String tipoVenta){this.tipoVenta=tipoVenta;}

    public Integer getCantidad(){return cantidad;}
    public void setCantidad(Integer cantidad){this.cantidad=cantidad;}

    public Integer getUnidadesDescontadas(){return unidadesDescontadas;}
    public void setUnidadesDescontadas(Integer unidadesDescontadas){this.unidadesDescontadas=unidadesDescontadas;}

    public BigDecimal getPrecioUnitario(){return precioUnitario;}
    public void setPrecioUnitario(BigDecimal precioUnitario){this.precioUnitario=precioUnitario;}

    public BigDecimal getSubtotal(){return subtotal;}
    public void setSubtotal(BigDecimal subtotal){this.subtotal=subtotal;}
}
