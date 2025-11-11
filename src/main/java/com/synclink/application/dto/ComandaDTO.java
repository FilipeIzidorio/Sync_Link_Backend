package com.synclink.application.dto;

import com.synclink.model.StatusComanda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para comanda")
public class ComandaDTO {

    private Long id;

    @Schema(description = "ID da mesa", example = "1")
    private Long mesaId;

    @Schema(description = "Número da mesa", example = "5")
    private Integer mesaNumero;

    @Schema(description = "Código único da comanda", example = "CMDABC123")
    private String codigo;

    @Schema(description = "Status da comanda")
    private StatusComanda status;

    @Schema(description = "Data de abertura")
    private LocalDateTime dataAbertura;

    @Schema(description = "Data de fechamento")
    private LocalDateTime dataFechamento;

    @Schema(description = "Total da comanda")
    private BigDecimal total;

    @Schema(description = "Pedidos associados à comanda")
    private List<PedidoDTO> pedidos;
}