package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para item do pedido")
public class ItemPedidoDTO {

    private Long id;

    @Schema(description = "ID do pedido", example = "1")
    @NotNull
    private Long pedidoId;

    @Schema(description = "ID do produto", example = "1")
    @NotNull
    private Long produtoId;

    @Schema(description = "Nome do produto")
    private String produtoNome;

    @Schema(description = "Preço do produto")
    private BigDecimal precoProduto;

    @Schema(description = "Quantidade", example = "2")
    @NotNull
    @Positive
    private Integer quantidade;

    @Schema(description = "Preço unitário", example = "25.90")
    @NotNull
    @Positive
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal do item")
    private BigDecimal subtotal;

    @Schema(description = "Observações do item")
    private String observacao;
}