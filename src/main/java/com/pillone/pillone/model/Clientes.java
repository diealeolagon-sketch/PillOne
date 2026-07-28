package com.pillone.pillone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clientes
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_bitacora;

    @NotBlank(message = "El radicado es obligatorio")
    private Integer id_radicado;

    @NotBlank(message = "el usuario es obligatoria")
    private Integer id_usuario ;

    @NotBlank(message = "la accion es obligatorio")
    private String accion ;

    @NotNull(message = "la descripcion es obligatorio")
    private String descripcion ;

    @NotNull(message = "la fecha es obligatorio")
    private String fecha ;







}

