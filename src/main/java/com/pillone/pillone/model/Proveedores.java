package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="proveedores")
public class Proveedores {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_proveedor")
    private Integer id_proveedor;

    @NotBlank(message="La razón social es obligatoria")
    @Column(name="razon_social",nullable=false)
    private String razon_social;

    @NotBlank(message="El NIT es obligatorio")
    @Column(name="nit",nullable=false,unique=true)
    private String nit;

    @NotBlank(message="La dirección es obligatoria")
    @Column(name="direccion",nullable=false)
    private String direccion;

    @NotBlank(message="El teléfono es obligatorio")
    @Column(name="telefono",nullable=false)
    private String telefono;

    @NotBlank(message="El correo es obligatorio")
    @Column(name="correo",nullable=false)
    private String correo;

    @NotBlank(message="El nombre de contacto es obligatorio")
    @Column(name="nombre_contacto",nullable=false)
    private String nombre_contacto;

    @NotBlank(message="El tipo de productos es obligatorio")
    @Column(name="tipo_productos",nullable=false)
    private String tipo_productos;

    @NotNull(message="El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name="estado",nullable=false)
    private EstadoProveedor estado=EstadoProveedor.ACTIVO;

    public enum EstadoProveedor{
        ACTIVO,
        INACTIVO
    }

    public Integer getId_proveedor(){
        return id_proveedor;
    }

    public void setId_proveedor(Integer id_proveedor){
        this.id_proveedor=id_proveedor;
    }

    public String getRazon_social(){
        return razon_social;
    }

    public void setRazon_social(String razon_social){
        this.razon_social=razon_social;
    }

    public String getNit(){
        return nit;
    }

    public void setNit(String nit){
        this.nit=nit;
    }

    public String getDireccion(){
        return direccion;
    }

    public void setDireccion(String direccion){
        this.direccion=direccion;
    }

    public String getTelefono(){
        return telefono;
    }

    public void setTelefono(String telefono){
        this.telefono=telefono;
    }

    public String getCorreo(){
        return correo;
    }

    public void setCorreo(String correo){
        this.correo=correo;
    }

    public String getNombre_contacto(){
        return nombre_contacto;
    }

    public void setNombre_contacto(String nombre_contacto){
        this.nombre_contacto=nombre_contacto;
    }

    public String getTipo_productos(){
        return tipo_productos;
    }

    public void setTipo_productos(String tipo_productos){
        this.tipo_productos=tipo_productos;
    }

    public EstadoProveedor getEstado(){
        return estado;
    }

    public void setEstado(EstadoProveedor estado){
        this.estado=estado;
    }
}