package com.synclink.domain.repository;

import com.synclink.model.Pedido;
import com.synclink.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByMesaId(Long mesaId);
    List<Pedido> findByStatusNot(StatusPedido status);

    @Query("SELECT p FROM Pedido p WHERE p.dataCriacao BETWEEN :inicio AND :fim")
    List<Pedido> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT p FROM Pedido p WHERE p.status IN :statuses ORDER BY p.dataCriacao DESC")
    List<Pedido> findByStatusIn(List<StatusPedido> statuses);

    Long countByStatus(StatusPedido status);

    @Query("SELECT p FROM Pedido p WHERE p.mesa.id = :mesaId AND p.status NOT IN :excludedStatuses")
    List<Pedido> findByMesaIdAndStatusNotIn(@Param("mesaId") Long mesaId,
                                            @Param("excludedStatuses") List<StatusPedido> excludedStatuses);

    @Query("SELECT p FROM Pedido p WHERE p.mesa.id = :mesaId AND p.status = 'ABERTO'")
    Optional<Pedido> findPedidoAbertoPorMesa(@Param("mesaId") Long mesaId);

    @Query("SELECT p FROM Pedido p WHERE p.status IN ('ABERTO', 'EM_PREPARO', 'PRONTO', 'ENTREGUE') ORDER BY p.dataCriacao DESC")
    List<Pedido> findPedidosAtivos();

    @Query("SELECT p FROM Pedido p WHERE p.mesa.id = :mesaId AND p.status IN ('ABERTO', 'EM_PREPARO', 'PRONTO', 'ENTREGUE')")
    Optional<Pedido> findPedidoAtivoPorMesa(@Param("mesaId") Long mesaId);
}