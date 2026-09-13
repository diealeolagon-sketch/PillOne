package com.pillone.pillone.repository;

import com.pillone.pillone.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios,Long> {

    Usuarios findByIdEmpleado(Long idEmpleado);

    Optional<Usuarios> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdUsuarioNot(
            String username,
            Long idUsuario
    );
}