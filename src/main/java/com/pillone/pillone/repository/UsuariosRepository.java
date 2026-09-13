package com.pillone.pillone.repository;

import com.pillone.pillone.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Usuarios findByIdEmpleado(Long idEmpleado);
    Optional<Usuarios> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdUsuarioNot(String username, Long idUsuario);

    @Query(value = """
        SELECT u.*
        FROM usuarios u
        INNER JOIN empleados e ON e.id_empleado=u.id_empleado
        WHERE LOWER(u.username)=LOWER(:login)
           OR LOWER(e.correo)=LOWER(:login)
        LIMIT 1
        """, nativeQuery = true)
    Optional<Usuarios> buscarParaLogin(@Param("login") String login);

    @Query(value = """
        SELECT u.*
        FROM usuarios u
        INNER JOIN empleados e ON e.id_empleado=u.id_empleado
        WHERE LOWER(e.correo)=LOWER(:correo)
        LIMIT 1
        """, nativeQuery = true)
    Optional<Usuarios> buscarPorCorreoEmpleado(@Param("correo") String correo);
}
