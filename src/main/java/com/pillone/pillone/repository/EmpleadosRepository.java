package com.pillone.pillone.repository;

import com.pillone.pillone.model.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmpleadosRepository extends JpaRepository<Empleados, Long> {

    // Utiliza este si en tu clase Empleado la relación es un objeto llamado "sucursal"
    List<Empleados> findBySucursal_IdSucursal(Long idSucursal);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Empleados e WHERE e.numero_documento = :numeroDocumento")
    boolean existsByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    //validaciones
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Empleados e WHERE e.numero_documento = :numeroDocumento " +
            "AND e.id_empleado <> :idEmpleado")
    boolean existsByNumeroDocumentoAndIdEmpleadoNot(
            @Param("numeroDocumento") String numeroDocumento,
            @Param("idEmpleado") Long idEmpleado
    );

}