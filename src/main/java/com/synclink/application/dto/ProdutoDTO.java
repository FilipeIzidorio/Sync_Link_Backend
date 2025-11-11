package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para produto")
public class ProdutoDTO {

    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    @NotBlank
    private String nome;

    @Schema(description = "Descrição do produto")
    private String descricao;

    @Schema(description = "Preço do produto", example = "45.90")
    @NotNull
    @Positive
    private BigDecimal preco;

    @Schema(description = "Status ativo")
    private Boolean ativo;

    @Schema(description = "ID da categoria", example = "1")
    @NotNull
    private Long categoriaId;

    @Schema(description = "Nome da categoria")
    private String categoriaNome;
}