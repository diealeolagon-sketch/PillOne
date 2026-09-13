package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="usuarios")
@Data
public class Usuarios {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Long idUsuario;

    @Column(name="id_empleado",nullable=false,unique=true)
    private Long idEmpleado;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_rol",nullable=false)
    private Roles rol;

    @Column(name="username",nullable=false,unique=true,length=50)
    private String username;

    @Column(name="password_hash",nullable=false,length=255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="estado",nullable=false,length=20)
    private EstadoUsuario estado=EstadoUsuario.ACTIVO;

    public enum EstadoUsuario {
        ACTIVO,
        BLOQUEADO,
        INACTIVO
    }
}