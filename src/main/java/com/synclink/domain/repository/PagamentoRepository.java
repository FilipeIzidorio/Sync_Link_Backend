package com.synclink.domain.repository;

import com.synclink.model.FormaPagamento;
import com.synclink.model.Pagamento;
import com.synclink.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedidoId(Long pedidoId);

    List<Pagamento> findByStatus(StatusPagamento status);

    @Query("SELECT p FROM Pagamento p WHERE p.dataCriacao BETWEEN :inicio AND :fim")
    List<Pagamento> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT p FROM Pagamento p WHERE p.formaPagamento = :formaPagamento")
    List<Pagamento> findByFormaPagamento(@Param("formaPagamento") FormaPagamento formaPagamento);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p " +
            "WHERE p.status = 'APROVADO' AND p.dataCriacao BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPagamentosPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
