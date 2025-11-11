package com.synclink.domain.repository;

import com.synclink.model.PerfilUsuario;
import com.synclink.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByPerfil(PerfilUsuario perfil);
    List<Usuario> findByAtivo(Boolean ativo);
}