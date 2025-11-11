package com.synclink.application.service;

import com.synclink.application.dto.ComandaDTO;

import java.util.List;

public interface ComandaService {

    ComandaDTO abrirComanda(Long mesaId);

    ComandaDTO fecharComanda(Long comandaId);

    ComandaDTO cancelarComanda(Long comandaId, String motivo);

    ComandaDTO findById(Long id);

    ComandaDTO findByCodigo(String codigo);

    List<ComandaDTO> findByMesaId(Long mesaId);

    List<ComandaDTO> findComandasAbertas();

    List<ComandaDTO> findByStatus(String status);

    ComandaDTO adicionarPedido(Long comandaId, Long pedidoId);

    ComandaDTO removerPedido(Long comandaId, Long pedidoId);
}
