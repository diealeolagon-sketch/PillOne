package com.pillone.pillone.repository;

import com.pillone.pillone.model.Domicilios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomiciliosRepository extends JpaRepository<Domicilios, Long> {
    Optional<Domicilios> findByVenta_IdVenta(Long idVenta);
}
