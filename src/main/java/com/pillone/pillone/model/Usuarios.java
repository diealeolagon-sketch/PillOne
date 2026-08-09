package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario; // Cambiado a camelCase

    @Column(name = "id_empleado", nullable = false, unique = true)
    private Long idEmpleado; // Cambiado a camelCase

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Roles rol; // Relacionado con Roles

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash; // Opcional, pero recomendado en camelCase (actualiza el @Column si tu BD usa password_hash)

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    public enum EstadoUsuario {
        ACTIVO, BLOQUEADO, INACTIVO
    }
}