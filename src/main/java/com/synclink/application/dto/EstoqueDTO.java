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
@Schema(description = "Objeto de transferência de dados para Estoque")
public class EstoqueDTO {

    @Schema(description = "Identificador único do estoque", example = "1")
    private Long id;

    @Schema(description = "ID do produto associado", example = "2", required = true)
    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;


    @Schema(description = "Quantidade atual em estoque", example = "100", required = true)
    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    @Schema(description = "Quantidade mínima para reposição", example = "10")
    @PositiveOrZero(message = "O estoque mínimo deve ser zero ou positivo")
    private Integer estoqueMinimo = 0;

    @Schema(description = "Quantidade máxima de segurança", example = "300")
    private Integer estoqueMaximo;

    @Schema(description = "Custo unitário de aquisição", example = "5.90")
    private BigDecimal custoUnitario;

    @Schema(description = "Data de entrada no estoque")
    private LocalDateTime dataEntrada;

    @Schema(description = "Data de validade do item", example = "2025-12-31T00:00:00")
    private LocalDateTime dataValidade;

    @Schema(description = "Número de lote", example = "LT-1025")
    private String lote;

    @Schema(description = "Indica se o item precisa de reposição", example = "false")
    private Boolean precisaRepor = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }


    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public void setEstoqueMaximo(Integer estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDateTime dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Boolean getPrecisaRepor() {
        return precisaRepor;
    }

    public void setPrecisaRepor(Boolean precisaRepor) {
        this.precisaRepor = precisaRepor;
    }
}
