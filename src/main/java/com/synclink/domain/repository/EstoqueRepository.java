package com.synclink.domain.repository;

import com.synclink.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    List<Estoque> findByProdutoId(Long produtoId);

    @Query("SELECT e FROM Estoque e WHERE e.quantidade <= e.estoqueMinimo")
    List<Estoque> findByQuantidadeLessThanEqualEstoqueMinimo();
}
