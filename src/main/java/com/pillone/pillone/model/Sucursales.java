package com.pillone.pillone.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursales")
public class Sucursales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @ManyToOne
    @JoinColumn(name = "id_empleado") // O el nombre de la columna que tengas en tu BD para el empleado
    private Empleados empleado;

    // No olvides su Getter y Setter:
    public Empleados getEmpleado() { return empleado; }
    public void setEmpleado(Empleados empleado) { this.empleado = empleado; }

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    // Aquí está la clave: la variable se llama codigo pero apunta a la columna codigo_sucursal de tu BD
    @Column(name = "codigo_sucursal", nullable = false, length = 30)
    private String codigo;

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "ciudad", length = 100)
    private String ciudad = "Tuluá";

    @Column(name = "departamento", length = 100)
    private String departamento = "Valle del Cauca";

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVA";

    public Sucursales() {}

    // Getters y Setters
    public Long getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Long idSucursal) { this.idSucursal = idSucursal; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}