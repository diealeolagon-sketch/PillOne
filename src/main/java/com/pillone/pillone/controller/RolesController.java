package com.pillone.pillone.controller;

import com.pillone.pillone.model.Permisos;
import com.pillone.pillone.model.Roles;
import com.pillone.pillone.repository.PermisosRepository;
import com.pillone.pillone.repository.RolesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    private final RolesRepository rolesRepository;
    private final PermisosRepository permisosRepository;

    public RolesController(RolesRepository rolesRepository, PermisosRepository permisosRepository) {
        this.rolesRepository = rolesRepository;
        this.permisosRepository = permisosRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAll() {
        return rolesRepository.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return rolesRepository.findById(id)
                .map(rol -> ResponseEntity.ok(toDto(rol)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/permisos")
    public List<Permisos> permisos() {
        return permisosRepository.findByActivoTrueOrderByModuloAscOrdenAscNombreAsc();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody RolRequest request) {
        if (request.nombre == null || request.nombre.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio."));
        }

        String nombre = normalizar(request.nombre);
        if (rolesRepository.existsByNombreIgnoreCase(nombre)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un rol con ese nombre."));
        }

        Roles rol = new Roles();
        rol.setNombre(nombre);
        rol.setPermisos(new LinkedHashSet<>(permisosRepository.findAllById(request.permissionIds == null ? List.of() : request.permissionIds)));
        return ResponseEntity.ok(toDto(rolesRepository.save(rol)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RolRequest request) {
        Roles rol = rolesRepository.findById(id).orElse(null);
        if (rol == null) return ResponseEntity.notFound().build();

        String nombre = normalizar(request.nombre);
        if (rolesRepository.existsByNombreIgnoreCaseAndIdRolNot(nombre, id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya existe otro rol con ese nombre."));
        }

        rol.setNombre(nombre);
        rol.setPermisos(new LinkedHashSet<>(permisosRepository.findAllById(request.permissionIds == null ? List.of() : request.permissionIds)));
        return ResponseEntity.ok(toDto(rolesRepository.save(rol)));
    }

    private Map<String, Object> toDto(Roles rol) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("idRol", rol.getIdRol());
        dto.put("nombre", rol.getNombre());
        dto.put("cantidadPermisos", rol.getCantidadPermisos());
        dto.put("permisos", rol.getPermisos().stream().map(Permisos::getCodigo).toList());
        return dto;
    }

    private String normalizar(String valor) {
        if (valor == null) return "";
        return valor.trim().toUpperCase()
                .replaceAll("[^A-Z0-9ÁÉÍÓÚÑ]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    public static class RolRequest {
        public String nombre;
        public List<Integer> permissionIds;
    }
}
