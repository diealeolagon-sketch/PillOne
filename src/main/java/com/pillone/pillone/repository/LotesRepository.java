package com.pillone.pillone.repository;

import com.pillone.pillone.model.Lotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LotesRepository extends JpaRepository<Lotes, Long> {

    @Query("""
        SELECT l FROM Lotes l
        WHERE l.idProducto = :idProducto
          AND l.cantidadActual > 0
          AND l.estado IN ('DISPONIBLE','PROXIMO_A_VENCER')
          AND l.fechaVencimiento >= :hoy
        ORDER BY l.fechaVencimiento ASC, l.idLote ASC
    """)
    List<Lotes> buscarLotesFEFO(
            @Param("idProducto") Long idProducto,
            @Param("hoy") LocalDate hoy
    );

    List<Lotes> findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(Long idProducto);

    boolean existsByIdProductoAndNumeroLoteIgnoreCase(
            Long idProducto,
            String numeroLote
    );

    boolean existsByIdProductoAndNumeroLoteIgnoreCaseAndIdLoteNot(
            Long idProducto,
            String numeroLote,
            Long idLote
    );

    @Query("""
        SELECT COALESCE(SUM(l.cantidadActual),0)
        FROM Lotes l
        WHERE l.idProducto = :idProducto
          AND l.cantidadActual > 0
          AND l.estado IN ('DISPONIBLE','PROXIMO_A_VENCER')
          AND l.fechaVencimiento >= :hoy
    """)
    Integer sumarStockDisponible(
            @Param("idProducto") Long idProducto,
            @Param("hoy") LocalDate hoy
    );
}