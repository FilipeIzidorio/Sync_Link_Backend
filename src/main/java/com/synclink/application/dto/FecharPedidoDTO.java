package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO para fechamento de pedido")
public class FecharPedidoDTO {

    @Schema(description = "Acréscimo a ser aplicado", example = "10.00")
    private BigDecimal acrescimo = BigDecimal.ZERO;

    @Schema(description = "Desconto a ser aplicado", example = "5.00")
    private BigDecimal desconto = BigDecimal.ZERO;

    @Schema(description = "Justificativa para o acréscimo")
    private String justificativaAcrescimo;

    @Schema(description = "Justificativa para o desconto")
    private String justificativaDesconto;

    // Construtores
    public FecharPedidoDTO() {}

    // Getters e Setters
    public BigDecimal getAcrescimo() { return acrescimo; }
    public void setAcrescimo(BigDecimal acrescimo) { this.acrescimo = acrescimo; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public String getJustificativaAcrescimo() { return justificativaAcrescimo; }
    public void setJustificativaAcrescimo(String justificativaAcrescimo) { this.justificativaAcrescimo = justificativaAcrescimo; }

    public String getJustificativaDesconto() { return justificativaDesconto; }
    public void setJustificativaDesconto(String justificativaDesconto) { this.justificativaDesconto = justificativaDesconto; }
}