package com.pillone.pillone.repository;

import com.pillone.pillone.model.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisosRepository extends JpaRepository<Permisos, Integer> {
    List<Permisos> findByActivoTrueOrderByModuloAscOrdenAscNombreAsc();
    List<Permisos> findByModuloAndActivoTrueOrderByOrdenAscNombreAsc(String modulo);
    Optional<Permisos> findByCodigoIgnoreCase(String codigo);
}
