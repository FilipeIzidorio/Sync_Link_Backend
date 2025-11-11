package com.synclink.application.dto;

import com.synclink.model.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para pedido")
public class PedidoDTO {

    private Long id;

    @Schema(description = "ID da mesa", example = "1")
    private Long mesaId;

    @Schema(description = "Número da mesa")
    private Integer mesaNumero;

    @Schema(description = "ID do usuário")
    private Long usuarioId;

    @Schema(description = "Nome do usuário")
    private String usuarioNome;

    @Schema(description = "Status do pedido")
    private StatusPedido status;

    @Schema(description = "Subtotal do pedido")
    private BigDecimal subtotal;

    @Schema(description = "Acréscimo aplicado")
    private BigDecimal acrescimo;

    @Schema(description = "Desconto aplicado")
    private BigDecimal desconto;

    @Schema(description = "Total do pedido")
    private BigDecimal total;

    @Schema(description = "Valor final")
    private BigDecimal valorFinal;

    @Schema(description = "Observações do pedido")
    private String observacao;

    @Schema(description = "Justificativa do acréscimo")
    private String justificativaAcrescimo;

    @Schema(description = "Justificativa do desconto")
    private String justificativaDesconto;

    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data de atualização")
    private LocalDateTime dataAtualizacao;

    @Schema(description = "Data de fechamento")
    private LocalDateTime dataFechamento;

    @Schema(description = "Itens do pedido")
    private List<ItemPedidoDTO> itens;
}