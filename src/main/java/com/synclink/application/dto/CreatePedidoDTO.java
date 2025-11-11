package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de pedido")
public class CreatePedidoDTO {

    @Schema(description = "ID da mesa", example = "1")
    @NotNull
    private Long mesaId;

    @Schema(description = "Observações do pedido")
    private String observacao;

    // Construtores
    public CreatePedidoDTO() {}

    public CreatePedidoDTO(Long mesaId, String observacao) {
        this.mesaId = mesaId;
        this.observacao = observacao;
    }

    // Getters e Setters

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}