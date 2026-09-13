package com.pillone.pillone.repository;

import com.pillone.pillone.model.Devoluciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevolucionesRepository extends JpaRepository<Devoluciones,Long> {
}
