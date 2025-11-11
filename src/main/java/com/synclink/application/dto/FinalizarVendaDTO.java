package com.synclink.application.dto;

import com.synclink.model.FormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para finalização de venda")
public class FinalizarVendaDTO {

    @Schema(description = "Forma de pagamento", required = true)
    @NotNull
    private FormaPagamento formaPagamento;

    @Schema(description = "Valor pago pelo cliente", required = true)
    @NotNull
    private BigDecimal valorPago;

    @Schema(description = "Observações sobre o pagamento")
    private String observacao;

    @Schema(description = "CPF/CNPJ do cliente para nota fiscal")
    private String documentoCliente;

    @Schema(description = "Nome do cliente")
    private String nomeCliente;
}