package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de mesa")
public class CreateMesaDTO {

    @Schema(description = "Número da mesa", example = "1")
    @NotNull
    private Integer numero;

    @Schema(description = "Descrição da mesa", example = "Mesa próxima à janela")
    private String descricao;

    // Construtores
    public CreateMesaDTO() {}

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}