package com.pillone.pillone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_empleado", nullable = false, unique = true)
    private Long idEmpleado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Roles rol;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    public enum EstadoUsuario {
        ACTIVO, BLOQUEADO, INACTIVO
    }

    public Long getIdUsuario(){return idUsuario;}
    public void setIdUsuario(Long idUsuario){this.idUsuario=idUsuario;}
    public Long getIdEmpleado(){return idEmpleado;}
    public void setIdEmpleado(Long idEmpleado){this.idEmpleado=idEmpleado;}
    public Roles getRol(){return rol;}
    public void setRol(Roles rol){this.rol=rol;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username=username;}
    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
    public EstadoUsuario getEstado(){return estado;}
    public void setEstado(EstadoUsuario estado){this.estado=estado;}
}
