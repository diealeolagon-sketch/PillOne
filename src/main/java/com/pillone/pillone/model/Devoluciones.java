package com.pillone.pillone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="devoluciones")
public class Devoluciones {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_devolucion")
    private Long idDevolucion;

    @Column(name="id_sucursal",nullable=false)
    private Long idSucursal;

    @Column(name="tipo_devolucion",nullable=false,length=20)
    private String tipoDevolucion;

    @Column(name="id_venta")
    private Long idVenta;

    @Column(name="id_compra")
    private Long idCompra;

    @Column(name="id_producto",nullable=false)
    private Long idProducto;

    @Column(name="id_lote")
    private Long idLote;

    @Column(name="cantidad",nullable=false)
    private Integer cantidad;

    @Column(name="motivo",nullable=false,length=40)
    private String motivo;

    @Column(name="estado_producto",nullable=false,length=40)
    private String estadoProducto;

    @Column(name="fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    @Column(name="id_usuario_regente",nullable=false)
    private Long idUsuarioRegente;

    @Column(name="observaciones",columnDefinition="TEXT")
    private String observaciones;

    @PrePersist
    public void prePersist(){
        if(fechaDevolucion==null) fechaDevolucion=LocalDateTime.now();
    }

    public Long getIdDevolucion(){return idDevolucion;}
    public void setIdDevolucion(Long idDevolucion){this.idDevolucion=idDevolucion;}
    public Long getIdSucursal(){return idSucursal;}
    public void setIdSucursal(Long idSucursal){this.idSucursal=idSucursal;}
    public String getTipoDevolucion(){return tipoDevolucion;}
    public void setTipoDevolucion(String tipoDevolucion){this.tipoDevolucion=tipoDevolucion;}
    public Long getIdVenta(){return idVenta;}
    public void setIdVenta(Long idVenta){this.idVenta=idVenta;}
    public Long getIdCompra(){return idCompra;}
    public void setIdCompra(Long idCompra){this.idCompra=idCompra;}
    public Long getIdProducto(){return idProducto;}
    public void setIdProducto(Long idProducto){this.idProducto=idProducto;}
    public Long getIdLote(){return idLote;}
    public void setIdLote(Long idLote){this.idLote=idLote;}
    public Integer getCantidad(){return cantidad;}
    public void setCantidad(Integer cantidad){this.cantidad=cantidad;}
    public String getMotivo(){return motivo;}
    public void setMotivo(String motivo){this.motivo=motivo;}
    public String getEstadoProducto(){return estadoProducto;}
    public void setEstadoProducto(String estadoProducto){this.estadoProducto=estadoProducto;}
    public LocalDateTime getFechaDevolucion(){return fechaDevolucion;}
    public void setFechaDevolucion(LocalDateTime fechaDevolucion){this.fechaDevolucion=fechaDevolucion;}
    public Long getIdUsuarioRegente(){return idUsuarioRegente;}
    public void setIdUsuarioRegente(Long idUsuarioRegente){this.idUsuarioRegente=idUsuarioRegente;}
    public String getObservaciones(){return observaciones;}
    public void setObservaciones(String observaciones){this.observaciones=observaciones;}
}
