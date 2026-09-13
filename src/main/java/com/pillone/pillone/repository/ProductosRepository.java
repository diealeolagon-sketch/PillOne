package com.pillone.pillone.repository;

import com.pillone.pillone.model.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductosRepository extends JpaRepository<Productos,Long> {

    boolean existsByCodigoInternoIgnoreCase(
            String codigoInterno
    );

    boolean existsByCodigoInternoIgnoreCaseAndIdProductoNot(
            String codigoInterno,
            Long idProducto
    );

    boolean existsByCodigoBarrasIgnoreCase(
            String codigoBarras
    );

    boolean existsByCodigoBarrasIgnoreCaseAndIdProductoNot(
            String codigoBarras,
            Long idProducto
    );
}