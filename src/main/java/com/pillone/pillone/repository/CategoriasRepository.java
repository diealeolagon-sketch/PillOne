package com.pillone.pillone.repository;

import com.pillone.pillone.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasRepository extends JpaRepository<Categorias,Integer> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdCategoriaNot(
            String nombre,
            Integer idCategoria
    );
}