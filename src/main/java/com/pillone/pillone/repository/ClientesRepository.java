package com.odin.odin.repository;

import com.odin.odin.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientesRepository extends JpaRepository<Clientes, Long>
{

}
