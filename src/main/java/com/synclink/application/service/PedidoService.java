package com.synclink.application.service;



import com.synclink.application.dto.*;
import com.synclink.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public interface PedidoService {

    // Métodos básicos de CRUD
    List<PedidoDTO> findAll();
    PedidoDTO findById(Long id);
    PedidoDTO create(CreatePedidoDTO createPedidoDTO, Long usuarioId);
    PedidoDTO update(Long id, PedidoDTO pedidoDTO);
    void delete(Long id);

    // Gestão de itens do pedido
    PedidoDTO adicionarItem(Long pedidoId, AdicionarItemPedidoDTO itemDTO);
    PedidoDTO removerItem(Long pedidoId, Long itemId);
    PedidoDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer novaQuantidade);

    // Controle de status
    PedidoDTO atualizarStatus(Long pedidoId, StatusPedido status);
    PedidoDTO moverParaPreparo(Long pedidoId);
    PedidoDTO marcarComoPronto(Long pedidoId);
    PedidoDTO marcarComoEntregue(Long pedidoId);
    PedidoDTO cancelarPedido(Long pedidoId, String motivoCancelamento);

    // Gestão financeira
    PedidoDTO aplicarAcrescimo(Long pedidoId, BigDecimal valor, String justificativa);
    PedidoDTO aplicarDesconto(Long pedidoId, BigDecimal valor, String justificativa);
    PedidoDTO fecharPedido(Long pedidoId, FecharPedidoDTO fecharPedidoDTO);

    // NOVOS MÉTODOS PARA FINALIZAÇÃO DE VENDA
    PedidoFinalizadoDTO finalizarVenda(Long pedidoId, FinalizarVendaDTO finalizarVendaDTO);
    BigDecimal calcularTroco(Long pedidoId, BigDecimal valorPago);
    PedidoDTO reabrirPedido(Long pedidoId, String motivo);

    // Consultas e relatórios
    List<PedidoDTO> findByStatus(StatusPedido status);
    List<PedidoDTO> findByMesaId(Long mesaId);
    List<PedidoDTO> findPedidosAtivos();
    List<PedidoDTO> findPedidosPorPeriodo(String dataInicio, String dataFim);
    List<PedidoDTO> findPedidosCozinha();
    List<PedidoResumidoDTO> findResumoVendasDiarias(String data);

    // Estatísticas
    EstatisticasPedidosDTO obterEstatisticas(String dataInicio, String dataFim);
    BigDecimal calcularTotalVendasPeriodo(String dataInicio, String dataFim);
}