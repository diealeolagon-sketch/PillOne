package com.pillone.pillone.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_formulas")
public class DetallesFormulas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_formula")
    private Long idDetalleFormula;

    @Column(name = "id_formula", nullable = false)
    private Long idFormula;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(name = "dosis", nullable = false, length = 100)
    private String dosis;

    @Column(name = "frecuencia", nullable = false, length = 100)
    private String frecuencia;

    @Column(name = "duracion_tratamiento", nullable = false, length = 100)
    private String duracionTratamiento;

    public Long getIdDetalleFormula() {
        return idDetalleFormula;
    }

    public void setIdDetalleFormula(Long idDetalleFormula) {
        this.idDetalleFormula = idDetalleFormula;
    }

    public Long getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(Long idFormula) {
        this.idFormula = idFormula;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracionTratamiento() {
        return duracionTratamiento;
    }

    public void setDuracionTratamiento(String duracionTratamiento) {
        this.duracionTratamiento = duracionTratamiento;
    }
}