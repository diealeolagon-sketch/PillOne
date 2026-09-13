package com.pillone.pillone.repository;

import com.pillone.pillone.model.PasswordResetToken;
import com.pillone.pillone.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long>{
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    List<PasswordResetToken> findAllByUsuarioAndUsadoFalse(Usuarios usuario);
}
