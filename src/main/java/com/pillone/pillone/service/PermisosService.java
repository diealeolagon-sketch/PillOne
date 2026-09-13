package com.pillone.pillone.service;

import com.pillone.pillone.model.Roles;
import com.pillone.pillone.repository.RolesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermisosService {
    private final RolesRepository rolesRepository;

    public PermisosService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Transactional(readOnly = true)
    public boolean tienePermiso(Integer idRol, String codigoPermiso) {
        if (idRol == null || codigoPermiso == null || codigoPermiso.isBlank()) return false;
        Roles rol = rolesRepository.findById(idRol).orElse(null);
        return rol != null && rol.tienePermiso(codigoPermiso);
    }

    @Transactional(readOnly = true)
    public boolean tieneTodos(Integer idRol, String... codigos) {
        if (codigos == null || codigos.length == 0) return true;
        Roles rol = rolesRepository.findById(idRol).orElse(null);
        if (rol == null) return false;
        for (String codigo : codigos) {
            if (!rol.tienePermiso(codigo)) return false;
        }
        return true;
    }
}
