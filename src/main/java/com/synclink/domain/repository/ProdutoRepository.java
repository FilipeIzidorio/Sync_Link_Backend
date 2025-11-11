package com.synclink.domain.repository;

import com.synclink.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByAtivo(Boolean ativo);

    boolean existsByNomeAndCategoriaId(String nome, Long categoriaId);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.nome ASC")
    List<Produto> findAllActiveOrderedByName();
}
