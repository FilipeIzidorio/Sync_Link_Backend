package com.synclink.application.dto;

import com.synclink.model.StatusPedido;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticasPedidosDTO {
    private Long totalPedidos;
    private BigDecimal totalVendas;
    private BigDecimal mediaPorPedido;
    private Long pedidosAbertos;
    private Long pedidosCozinha;
    private Long pedidosEntregues;
    private Map<StatusPedido, Long> pedidosPorStatus;
    private Map<String, BigDecimal> vendasPorFormaPagamento;
}