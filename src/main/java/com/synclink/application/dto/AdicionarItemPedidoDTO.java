package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO para adicionar item ao pedido")
public class AdicionarItemPedidoDTO {

    @Schema(description = "ID do produto", example = "1")
    @NotNull
    private Long produtoId;

    @Schema(description = "Quantidade do produto", example = "2")
    @NotNull
    @Positive
    private Integer quantidade;

    @Schema(description = "Observações do item")
    private String observacao;

    // Construtores
    public AdicionarItemPedidoDTO() {}

    public AdicionarItemPedidoDTO(Long produtoId, Integer quantidade, String observacao) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.observacao = observacao;
    }

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}