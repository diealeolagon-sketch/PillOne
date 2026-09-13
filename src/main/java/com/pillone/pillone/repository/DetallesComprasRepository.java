package com.pillone.pillone.repository;

import com.pillone.pillone.model.DetallesCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesComprasRepository
        extends JpaRepository<DetallesCompras,Integer>{

    List<DetallesCompras> findByIdCompra(
            Integer idCompra
    );

    void deleteByIdCompra(
            Integer idCompra
    );
}