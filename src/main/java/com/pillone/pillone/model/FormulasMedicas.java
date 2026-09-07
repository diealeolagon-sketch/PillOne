package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "formulas_medicas")
public class FormulasMedicas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formula")
    private Long idFormula;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "nombre_medico", nullable = false, length = 150)
    private String nombreMedico;

    @Column(name = "tarjeta_profesional", nullable = false, length = 50)
    private String tarjetaProfesional;

    @Column(name = "entidad_salud", length = 100)
    private String entidadSalud;

    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "vigencia_dias")
    private Integer vigenciaDias = 30;

    @Column(name = "archivo_adjunto", length = 255)
    private String archivoAdjunto;

    private String observaciones;

    // Campo transitorio para que Hibernate no busque la columna 'estado' en la tabla
    @Transient
    private String estado;
}