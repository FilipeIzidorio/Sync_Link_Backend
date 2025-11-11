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
@Schema(description = "DTO que representa um item pertencente a um pedido")
public class ItemPedidoDTO {

    private Long id;

    @NotNull
    @Schema(description = "ID do pedido associado", example = "1")
    private Long pedidoId;

    @NotNull
    @Schema(description = "ID do produto", example = "3")
    private Long produtoId;

    @Schema(description = "Nome do produto", example = "Pizza Calabresa")
    private String produtoNome;

    @Schema(description = "Preço do produto no momento da venda", example = "45.90")
    private BigDecimal precoProduto;

    @NotNull
    @Positive
    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;

    @NotNull
    @Positive
    @Schema(description = "Preço unitário", example = "22.95")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal calculado do item (quantidade * preço)", example = "45.90")
    private BigDecimal subtotal;

    @Schema(description = "Observações do item (ex: extra queijo)")
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public BigDecimal getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(BigDecimal precoProduto) {
        this.precoProduto = precoProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
