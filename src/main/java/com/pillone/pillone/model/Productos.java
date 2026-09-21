package com.pillone.pillone.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "codigo_interno", nullable = false, unique = true, insertable = false, updatable = false)
    private String codigoInterno;

    @Column(name = "codigo_barras", nullable = false, unique = true)
    private String codigoBarras;

    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    @Column(name = "nombre_generico")
    private String nombreGenerico;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "presentacion")
    private String presentacion;

    @Column(name = "concentracion")
    private String concentracion;

    @Column(name = "laboratorio")
    private String laboratorio;

    @Column(name = "registro_invima", nullable = false)
    private String registroInvima;

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "porcentaje_iva", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeIva = BigDecimal.ZERO;

    @Column(name = "precio_venta_tableta")
    private Double precioVentaTableta;

    @Column(name = "precio_venta_sello")
    private Double precioVentaSello;

    @Column(name = "unidades_por_empaque")
    private Integer unidadesPorEmpaque;

    @Column(name = "sellos_por_caja")
    private Integer sellosPorCaja;

    @Column(name = "unidades_por_sello")
    private Integer unidadesPorSello;

    @Column(name = "precio_compra_empaque")
    private Double precioCompraEmpaque;

    @Column(name = "precio_venta_empaque")
    private Double precioVentaEmpaque;

    @Column(name = "stock_total")
    private Integer stockTotal;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "ubicacion_estante")
    private String ubicacionEstante;

    @Column(name = "requiere_formula")
    private Boolean requiereFormula;

    @Column(name = "es_venta_libre")
    private Boolean esVentaLibre;

    @Column(name = "es_controlado")
    private Boolean esControlado;

    @Column(name = "requiere_refrigeracion")
    private Boolean requiereRefrigeracion;

    @Column(name = "restricciones_venta")
    private String restriccionesVenta;

    @Column(name = "estado")
    private String estado;

    public Productos() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getRegistroInvima() {
        return registroInvima;
    }

    public void setRegistroInvima(String registroInvima) {
        this.registroInvima = registroInvima;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public Double getPrecioVentaTableta() {
        return precioVentaTableta;
    }

    public void setPrecioVentaTableta(Double precioVentaTableta) {
        this.precioVentaTableta = precioVentaTableta;
    }

    public Double getPrecioVentaSello() {
        return precioVentaSello;
    }

    public void setPrecioVentaSello(Double precioVentaSello) {
        this.precioVentaSello = precioVentaSello;
    }

    public Integer getUnidadesPorEmpaque() {
        return unidadesPorEmpaque;
    }

    public void setUnidadesPorEmpaque(Integer unidadesPorEmpaque) {
        this.unidadesPorEmpaque = unidadesPorEmpaque;
    }

    public Integer getSellosPorCaja() {
        return sellosPorCaja;
    }

    public void setSellosPorCaja(Integer sellosPorCaja) {
        this.sellosPorCaja = sellosPorCaja;
    }

    public Integer getUnidadesPorSello() {
        return unidadesPorSello;
    }

    public void setUnidadesPorSello(Integer unidadesPorSello) {
        this.unidadesPorSello = unidadesPorSello;
    }

    public Double getPrecioCompraEmpaque() {
        return precioCompraEmpaque;
    }

    public void setPrecioCompraEmpaque(Double precioCompraEmpaque) {
        this.precioCompraEmpaque = precioCompraEmpaque;
    }

    public Double getPrecioVentaEmpaque() {
        return precioVentaEmpaque;
    }

    public void setPrecioVentaEmpaque(Double precioVentaEmpaque) {
        this.precioVentaEmpaque = precioVentaEmpaque;
    }

    public Integer getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUbicacionEstante() {
        return ubicacionEstante;
    }

    public void setUbicacionEstante(String ubicacionEstante) {
        this.ubicacionEstante = ubicacionEstante;
    }

    public Boolean getRequiereFormula() {
        return requiereFormula;
    }

    public void setRequiereFormula(Boolean requiereFormula) {
        this.requiereFormula = requiereFormula;
    }

    public Boolean getEsVentaLibre() {
        return esVentaLibre;
    }

    public void setEsVentaLibre(Boolean esVentaLibre) {
        this.esVentaLibre = esVentaLibre;
    }

    public Boolean getEsControlado() {
        return esControlado;
    }

    public void setEsControlado(Boolean esControlado) {
        this.esControlado = esControlado;
    }

    public Boolean getRequiereRefrigeracion() {
        return requiereRefrigeracion;
    }

    public void setRequiereRefrigeracion(Boolean requiereRefrigeracion) {
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    public String getRestriccionesVenta() {
        return restriccionesVenta;
    }

    public void setRestriccionesVenta(String restriccionesVenta) {
        this.restriccionesVenta = restriccionesVenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Transient
    public boolean tieneProveedorHabitual() {
        return idProveedor != null;
    }

}