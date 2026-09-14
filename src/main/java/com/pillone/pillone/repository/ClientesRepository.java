package com.pillone.pillone.repository;

import com.pillone.pillone.model.Clientes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientesRepository extends JpaRepository<Clientes, Long>
{
    @Query("""
        SELECT c
        FROM Clientes c
        WHERE LOWER(c.nombreCompleto) LIKE LOWER(CONCAT('%', :q, '%'))
           OR c.numeroDocumento LIKE CONCAT('%', :q, '%')
           OR c.telefono LIKE CONCAT('%', :q, '%')
           OR LOWER(COALESCE(c.correo, '')) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY c.nombreCompleto ASC
        """)
    List<Clientes> buscar(@Param("q") String q, Pageable pageable);
}
