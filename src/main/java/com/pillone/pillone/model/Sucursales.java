package com.pillone.pillone.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursales")
public class Sucursales{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 20)
    private String estado = "ACTIVO";

    // Constructor por defecto
    public Sucursales() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}