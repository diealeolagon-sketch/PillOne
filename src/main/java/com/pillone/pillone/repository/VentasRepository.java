package com.pillone.pillone.repository;

import com.pillone.pillone.model.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
    // Aquí puedes añadir consultas personalizadas si las necesitas más adelante
}