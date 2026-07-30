package com.pillone.pillone.repository;

import com.pillone.pillone.model.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadosRepository extends JpaRepository<Empleados, Long>
{

}