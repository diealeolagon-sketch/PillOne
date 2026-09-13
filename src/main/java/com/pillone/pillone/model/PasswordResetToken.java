package com.pillone.pillone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_token")
    private Long idToken;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="id_usuario",nullable=false)
    private Usuarios usuario;

    @Column(name="token_hash",nullable=false,unique=true,length=64)
    private String tokenHash;

    @Column(name="fecha_creacion",nullable=false)
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_expiracion",nullable=false)
    private LocalDateTime fechaExpiracion;

    @Column(name="usado",nullable=false)
    private Boolean usado=false;

    public boolean esValido(){
        return Boolean.FALSE.equals(usado) && fechaExpiracion!=null && fechaExpiracion.isAfter(LocalDateTime.now());
    }

    public Long getIdToken(){return idToken;}
    public void setIdToken(Long idToken){this.idToken=idToken;}
    public Usuarios getUsuario(){return usuario;}
    public void setUsuario(Usuarios usuario){this.usuario=usuario;}
    public String getTokenHash(){return tokenHash;}
    public void setTokenHash(String tokenHash){this.tokenHash=tokenHash;}
    public LocalDateTime getFechaCreacion(){return fechaCreacion;}
    public void setFechaCreacion(LocalDateTime fechaCreacion){this.fechaCreacion=fechaCreacion;}
    public LocalDateTime getFechaExpiracion(){return fechaExpiracion;}
    public void setFechaExpiracion(LocalDateTime fechaExpiracion){this.fechaExpiracion=fechaExpiracion;}
    public Boolean getUsado(){return usado;}
    public void setUsado(Boolean usado){this.usado=usado;}
}
