package com.synclink.application.dto;

import com.synclink.model.StatusMesa;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO leve para exibição rápida das mesas no salão.
 * Contém apenas as informações essenciais: número e status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO resumido da mesa para exibição no salão")
public class MesaResumoDTO {


    @Schema(description = "Número identificador da mesa", example = "12")
    private Integer numero;

    @Schema(description = "Status atual da mesa (ex: LIVRE, OCUPADA, RESERVADA)")
    private StatusMesa status;

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }
}
