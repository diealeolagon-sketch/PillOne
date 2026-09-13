package com.pillone.pillone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permisos",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    @OrderBy("modulo ASC, orden ASC, nombre ASC")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Permisos> permisos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Usuarios> usuarios;

    @Transient
    public int getCantidadPermisos() {
        return permisos == null ? 0 : permisos.size();
    }

    public boolean tienePermiso(String codigo) {
        if (codigo == null || permisos == null) return false;
        return permisos.stream().anyMatch(p -> codigo.equalsIgnoreCase(p.getCodigo()));
    }
}
