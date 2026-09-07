package com.pillone.pillone.repository;

import com.pillone.pillone.model.DetallesFormulas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesFormulasRepository extends JpaRepository<DetallesFormulas, Long> {
    List findByIdFormula(Long idFormula);
}