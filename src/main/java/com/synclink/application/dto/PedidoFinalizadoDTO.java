package com.synclink.application.dto;

import com.synclink.model.FormaPagamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoFinalizadoDTO {
    private Long pedidoId;
    private Long mesaId;
    private Integer mesaNumero;
    private BigDecimal total;
    private BigDecimal valorPago;
    private BigDecimal troco;
    private FormaPagamento formaPagamento;
    private String codigoTransacao;
    private LocalDateTime dataFechamento;
    private String nomeCliente;
    private String documentoCliente;
}