package com.synclink.application.dto;

import com.synclink.model.StatusPedido;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResumidoDTO {
    private Long id;
    private Integer mesaNumero;
    private StatusPedido status;
    private BigDecimal total;
    private LocalDateTime dataCriacao;
    private String usuarioNome;
}