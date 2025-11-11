package com.synclink.domain.repository;

import com.synclink.model.Comanda;
import com.synclink.model.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByMesaId(Long mesaId);

    List<Comanda> findByStatus(StatusComanda status);

    Optional<Comanda> findByCodigo(String codigo);

    List<Comanda> findByMesaIdAndStatus(Long mesaId, StatusComanda status);
}
