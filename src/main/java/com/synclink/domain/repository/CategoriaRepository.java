package com.synclink.domain.repository;

import com.synclink.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<Categoria> findByNomeIgnoreCase(String nome);
    List<Categoria> findByAtivo(Boolean ativo);
}
