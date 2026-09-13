package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="categorias")
public class Categorias {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_categoria")
    private Integer idCategoria;

    @NotBlank(message="El nombre de la categoría es obligatorio")
    @Column(name="nombre",nullable=false,length=100,unique=true)
    private String nombre;

    @Column(name="descripcion",columnDefinition="TEXT")
    private String descripcion;

    public Categorias(){}

    public Integer getIdCategoria(){
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria){
        this.idCategoria=idCategoria;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }
}