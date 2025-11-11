package com.synclink.application.dto;

import com.synclink.model.StatusMesa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para mesa")
public class MesaDTO {

    private Long id;

    @Schema(description = "Número da mesa", example = "1")
    @NotNull
    private Integer numero;

    @Schema(description = "Status da mesa")
    private StatusMesa status;

    @Schema(description = "Descrição da mesa")
    private String descricao;

    @Schema(description = "Indica se tem pedido ativo")
    private Boolean temPedidoAtivo;

    @Schema(description = "ID do pedido ativo")
    private Long pedidoAtivoId;
}