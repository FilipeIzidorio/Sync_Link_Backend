package com.synclink.application.dto;

import com.synclink.model.enums.StatusComanda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para representação de uma comanda")
public class ComandaDTO {

    private Long id;

    @Schema(description = "ID da mesa associada", example = "1")
    private Long mesaId;

    @Schema(description = "Número da mesa", example = "5")
    private Integer mesaNumero;

    @Schema(description = "Código único da comanda", example = "CMD12345")
    private String codigo;

    @Schema(description = "Status atual da comanda", example = "ABERTA")
    private StatusComanda status;

    @Schema(description = "Data de abertura da comanda")
    private LocalDateTime dataAbertura;

    @Schema(description = "Data de fechamento da comanda")
    private LocalDateTime dataFechamento;

    @Schema(description = "Valor total calculado da comanda")
    private BigDecimal total;

    @Schema(description = "Lista de pedidos associados à comanda")
    private List<PedidoDTO> pedidos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public Integer getMesaNumero() {
        return mesaNumero;
    }

    public void setMesaNumero(Integer mesaNumero) {
        this.mesaNumero = mesaNumero;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public StatusComanda getStatus() {
        return status;
    }

    public void setStatus(StatusComanda status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<PedidoDTO> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<PedidoDTO> pedidos) {
        this.pedidos = pedidos;
    }
}
