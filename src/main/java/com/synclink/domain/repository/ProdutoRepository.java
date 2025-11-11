package com.synclink.domain.repository;

import com.synclink.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    Optional<Produto> findByIdAndAtivoTrue(Long id);
    List<Produto> findByCategoriaId(Long categoriaId);
    List<Produto> findByAtivo(Boolean ativo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.nome ASC")
    List<Produto> findAllActiveOrderedByName();

    boolean existsByNomeAndCategoriaId(String nome, Long categoriaId);
}