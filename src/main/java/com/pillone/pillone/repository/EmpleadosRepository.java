package com.pillone.pillone.repository;

import com.pillone.pillone.model.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpleadosRepository extends JpaRepository<Empleados, Long> {

    // Utiliza este si en tu clase Empleado la relación es un objeto llamado "sucursal"
    List<Empleados> findBySucursal_IdSucursal(Long idSucursal);

}