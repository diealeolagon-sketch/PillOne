package com.pillone.pillone.model;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    @Column(name = "archivo_adjunto_url", length = 255)
    private String archivoAdjuntoUrl;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /*
     * Estado auxiliar para interfaz.
     * No se guarda en la base de datos.
     */
    @Transient
    private String estado;

    public FormulasMedicas() {
    }

    public Long getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(Long idFormula) {
        this.idFormula = idFormula;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getTarjetaProfesional() {
        return tarjetaProfesional;
    }

    public void setTarjetaProfesional(String tarjetaProfesional) {
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public String getEntidadSalud() {
        return entidadSalud;
    }

    public void setEntidadSalud(String entidadSalud) {
        this.entidadSalud = entidadSalud;
    }

    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(LocalDate fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public Integer getVigenciaDias() {
        return vigenciaDias;
    }

    public void setVigenciaDias(Integer vigenciaDias) {
        this.vigenciaDias = vigenciaDias;
    }

    public String getArchivoAdjuntoUrl() {
        return archivoAdjuntoUrl;
    }

    public void setArchivoAdjuntoUrl(String archivoAdjuntoUrl) {
        this.archivoAdjuntoUrl = archivoAdjuntoUrl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /*
     * Fecha calculada de vencimiento.
     * No se guarda en la base.
     */
    @Transient
    public LocalDate getFechaVencimiento() {

        if (fechaExpedicion == null) {
            return null;
        }

        int dias =
                vigenciaDias != null
                        &&
                        vigenciaDias > 0
                        ?
                        vigenciaDias
                        :
                        30;

        return fechaExpedicion.plusDays(
                dias
        );
    }

    /*
     * Indica si la fórmula está vigente hoy.
     */
    @Transient
    public boolean isVigente() {

        return isVigente(
                LocalDate.now()
        );
    }

    /*
     * Permite comprobar vigencia para una fecha específica.
     */
    public boolean isVigente(
            LocalDate fecha
    ) {

        if (
                fecha == null
                        ||
                        fechaExpedicion == null
        ) {

            return false;
        }

        LocalDate fechaVencimiento =
                getFechaVencimiento();

        if (fechaVencimiento == null) {
            return false;
        }

        return
                !fecha.isBefore(
                        fechaExpedicion
                )
                        &&
                        !fecha.isAfter(
                                fechaVencimiento
                        );
    }
}