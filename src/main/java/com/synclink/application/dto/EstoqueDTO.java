package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para estoque")
public class EstoqueDTO {

    private Long id;

    @Schema(description = "ID do produto", example = "1")
    @NotNull
    private Long produtoId;

    @Schema(description = "Nome do produto")
    private String produtoNome;

    @Schema(description = "Quantidade em estoque", example = "100")
    @NotNull
    @PositiveOrZero
    private Integer quantidade;

    @Schema(description = "Estoque mínimo", example = "10")
    @NotNull
    @PositiveOrZero
    private Integer estoqueMinimo;

    @Schema(description = "Estoque máximo", example = "200")
    private Integer estoqueMaximo;

    @Schema(description = "Custo unitário", example = "5.50")
    private BigDecimal custoUnitario;

    @Schema(description = "Data de entrada")
    private LocalDateTime dataEntrada;

    @Schema(description = "Data de validade")
    private LocalDateTime dataValidade;

    @Schema(description = "Número do lote")
    private String lote;

    @Schema(description = "Indica se precisa repor estoque")
    private Boolean precisaRepor;
}