package com.pillone.pillone.repository;

import com.pillone.pillone.model.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
    List<Ventas> findAllByOrderByFechaVentaDescIdVentaDesc();
}