package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumo estatístico de pedidos em um período")
public class EstatisticasPedidosDTO {

    @Schema(description = "Total de pedidos realizados no período")
    private Long totalPedidos;

    @Schema(description = "Total de pedidos cancelados no período")
    private Long totalCancelados;

    @Schema(description = "Valor total vendido no período")
    private BigDecimal totalVendas;

    @Schema(description = "Data de início do período analisado")
    private String periodoInicio;

    @Schema(description = "Data de fim do período analisado")
    private String periodoFim;

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public Long getTotalCancelados() {
        return totalCancelados;
    }

    public void setTotalCancelados(Long totalCancelados) {
        this.totalCancelados = totalCancelados;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas;
    }

    public String getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(String periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public String getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(String periodoFim) {
        this.periodoFim = periodoFim;
    }
}
