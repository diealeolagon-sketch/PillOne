package com.pillone.pillone.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="compras")
public class Compras {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_compra")
    private Integer idCompra;

    @Column(name="id_sucursal",nullable=false)
    private Integer idSucursal;

    @Column(name="numero_factura_proveedor",nullable=false,length=50)
    private String numeroFacturaProveedor;

    @Column(name="id_proveedor",nullable=false)
    private Integer idProveedor;

    @Column(name="id_empleado",nullable=false)
    private Integer idEmpleado;

    @Column(name="fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name="subtotal",nullable=false,precision=12,scale=2)
    private BigDecimal subtotal;

    @Column(name="impuestos",nullable=false,precision=12,scale=2)
    private BigDecimal impuestos=BigDecimal.ZERO;

    @Column(name="total",nullable=false,precision=12,scale=2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name="forma_pago")
    private FormaPago formaPago=FormaPago.CREDITO_PROVEEDOR;

    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private EstadoCompra estado=EstadoCompra.PENDIENTE;

    public enum FormaPago{
        EFECTIVO,
        TRANSFERENCIA,
        CREDITO_PROVEEDOR
    }

    public enum EstadoCompra{
        PENDIENTE,
        RECIBIDA,
        PARCIALMENTE_RECIBIDA,
        CANCELADA,
        DEVUELTA
    }

    @PrePersist
    public void prePersist(){
        if(fechaCompra==null){
            fechaCompra=LocalDateTime.now();
        }

        if(subtotal==null){
            subtotal=BigDecimal.ZERO;
        }

        if(impuestos==null){
            impuestos=BigDecimal.ZERO;
        }

        if(total==null){
            total=BigDecimal.ZERO;
        }

        if(formaPago==null){
            formaPago=FormaPago.CREDITO_PROVEEDOR;
        }

        if(estado==null){
            estado=EstadoCompra.PENDIENTE;
        }
    }

    public Integer getIdCompra(){
        return idCompra;
    }

    public void setIdCompra(Integer idCompra){
        this.idCompra=idCompra;
    }

    public Integer getIdSucursal(){
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal){
        this.idSucursal=idSucursal;
    }

    public String getNumeroFacturaProveedor(){
        return numeroFacturaProveedor;
    }

    public void setNumeroFacturaProveedor(String numeroFacturaProveedor){
        this.numeroFacturaProveedor=numeroFacturaProveedor;
    }

    public Integer getIdProveedor(){
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor){
        this.idProveedor=idProveedor;
    }

    public Integer getIdEmpleado(){
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado){
        this.idEmpleado=idEmpleado;
    }

    public LocalDateTime getFechaCompra(){
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra){
        this.fechaCompra=fechaCompra;
    }

    public BigDecimal getSubtotal(){
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal){
        this.subtotal=subtotal;
    }

    public BigDecimal getImpuestos(){
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos){
        this.impuestos=impuestos;
    }

    public BigDecimal getTotal(){
        return total;
    }

    public void setTotal(BigDecimal total){
        this.total=total;
    }

    public FormaPago getFormaPago(){
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago){
        this.formaPago=formaPago;
    }

    public EstadoCompra getEstado(){
        return estado;
    }

    public void setEstado(EstadoCompra estado){
        this.estado=estado;
    }
}