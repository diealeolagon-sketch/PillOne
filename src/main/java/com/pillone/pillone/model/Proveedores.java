//by Jacob Mafla
package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "proveedores")
public class Proveedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_proveedor;

    @NotBlank(message = "la razon social es obligatoria")
    private String razon_social;

    @NotBlank(message = "el nit es obligatorio")
    private String nit;

    @NotBlank(message = "la direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "el telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "el correo es obligatorio")
    private String correo;

    @NotBlank(message = "el nombre_contacto es obligatorio")
    private String nombre_contacto;

    @NotBlank(message = "los tipo_productos son obligatorios")
    private String tipo_productos;

    @NotNull(message = "el estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoProveedor estado;

    public void setId_provedor(Integer id) {
    }

    // --- ENUM ---
    public enum EstadoProveedor {
        ACTIVO,
        INACTIVO
    }

    // --- GETTERS Y SETTERS ---

    public Integer getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(Integer id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre_contacto() {
        return nombre_contacto;
    }

    public void setNombre_contacto(String nombre_contacto) {
        this.nombre_contacto = nombre_contacto;
    }

    public String getTipo_productos() {
        return tipo_productos;
    }

    public void setTipo_productos(String tipo_productos) {
        this.tipo_productos = tipo_productos;
    }

    public EstadoProveedor getEstado() {
        return estado;
    }

    public void setEstado(EstadoProveedor estado) {
        this.estado = estado;
    }
}