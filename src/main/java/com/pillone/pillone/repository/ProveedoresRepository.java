package com.pillone.pillone.repository;

import com.pillone.pillone.model.Proveedores;
import com.pillone.pillone.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedoresRepository extends JpaRepository<Proveedores, Integer>
{

}