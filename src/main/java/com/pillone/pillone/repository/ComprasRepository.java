package com.pillone.pillone.repository;

import com.pillone.pillone.model.Compras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprasRepository
        extends JpaRepository<Compras,Integer>{

    List<Compras> findAllByOrderByFechaCompraDesc();

    List<Compras> findByIdProveedorOrderByFechaCompraDesc(
            Integer idProveedor
    );
}