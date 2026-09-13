package com.pillone.pillone.repository;

import com.pillone.pillone.model.Sucursales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SucursalesRepository extends JpaRepository<Sucursales,Long> {

    List<Sucursales> findAllByOrderByEstadoAscNombreAsc();

    long countByEstado(String estado);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdSucursalNot(String codigo,Long idSucursal);

    Optional<Sucursales> findByCodigoIgnoreCase(String codigo);
}