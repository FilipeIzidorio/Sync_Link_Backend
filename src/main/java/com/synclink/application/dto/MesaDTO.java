package com.synclink.application.dto;

import com.synclink.model.enums.StatusMesa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa uma mesa do restaurante")
public class MesaDTO {

    private Long id;

    @NotNull
    @Schema(description = "Número identificador da mesa", example = "12")
    private Integer numero;

    @Schema(description = "Status atual da mesa", example = "OCUPADA")
    private StatusMesa status;

    @Schema(description = "Descrição da mesa (ex: área externa)")
    private String descricao;

    @Schema(description = "Indica se a mesa possui pedido ativo")
    private Boolean temPedidoAtivo;

    @Schema(description = "ID do pedido ativo, caso exista")
    private Long pedidoAtivoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getTemPedidoAtivo() {
        return temPedidoAtivo;
    }

    public void setTemPedidoAtivo(Boolean temPedidoAtivo) {
        this.temPedidoAtivo = temPedidoAtivo;
    }

    public Long getPedidoAtivoId() {
        return pedidoAtivoId;
    }

    public void setPedidoAtivoId(Long pedidoAtivoId) {
        this.pedidoAtivoId = pedidoAtivoId;
    }
}
