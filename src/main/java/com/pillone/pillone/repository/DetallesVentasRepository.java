package com.pillone.pillone.repository;

import com.pillone.pillone.model.DetallesVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesVentasRepository extends JpaRepository<DetallesVentas, Long> {

    // Método clave para buscar las líneas de productos asociadas a una venta específica (para el detalle y la regla FEFO)
    List<DetallesVentas> findByVenta_IdVenta(Long idVenta);
}