package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
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

    @Column(nullable = false, length = 100)
    private String dosis;

    @Column(nullable = false, length = 100)
    private String frecuencia;

    @Column(name = "duracion_tratamiento", nullable = false, length = 100)
    private String duracionTratamiento;
}