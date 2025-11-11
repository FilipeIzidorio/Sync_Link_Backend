package com.synclink.application.service;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.model.enums.FormaPagamento;
import com.synclink.model.enums.StatusPagamento;

import java.math.BigDecimal;
import java.util.List;

public interface PagamentoService {

    // Métodos básicos de CRUD
    List<PagamentoDTO> findAll();
    PagamentoDTO findById(Long id);
    PagamentoDTO create(PagamentoDTO pagamentoDTO);
    PagamentoDTO update(Long id, PagamentoDTO pagamentoDTO);
    void delete(Long id);

    // Processamento de pagamentos
    PagamentoDTO processarPagamento(Long pedidoId, FormaPagamento formaPagamento, BigDecimal valor);
    PagamentoDTO processarPagamentoCompleto(Long pedidoId, PagamentoDTO pagamentoDTO);
    PagamentoDTO estornarPagamento(Long pagamentoId);
    PagamentoDTO confirmarPagamento(Long pagamentoId);
    PagamentoDTO recusarPagamento(Long pagamentoId, String motivo);

    // Consultas
    List<PagamentoDTO> findByPedidoId(Long pedidoId);
    List<PagamentoDTO> findByStatus(StatusPagamento status);
    List<PagamentoDTO> findByFormaPagamento(FormaPagamento formaPagamento);
    List<PagamentoDTO> findPagamentosPorPeriodo(String dataInicio, String dataFim);

    // Relatórios e estatísticas
    BigDecimal calcularTotalPagamentosPeriodo(String dataInicio, String dataFim);
    List<PagamentoDTO> findPagamentosDia(String data);
}