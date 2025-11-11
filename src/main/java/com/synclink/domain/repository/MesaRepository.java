package com.synclink.domain.repository;

import com.synclink.model.Mesa;
import com.synclink.model.StatusMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findByStatus(StatusMesa status);

    Optional<Mesa> findByNumero(Integer numero);

    boolean existsByNumero(Integer numero);
}
