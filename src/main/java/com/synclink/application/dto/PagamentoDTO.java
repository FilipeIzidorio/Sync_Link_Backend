package com.synclink.application.dto;

import com.synclink.model.FormaPagamento;
import com.synclink.model.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para pagamento")
public class PagamentoDTO {

    private Long id;

    @Schema(description = "ID do pedido", example = "1")
    private Long pedidoId;

    @Schema(description = "Total do pedido")
    private BigDecimal pedidoTotal;

    @Schema(description = "Forma de pagamento", example = "CARTAO_CREDITO")
    private FormaPagamento formaPagamento;

    @Schema(description = "Valor do pagamento", example = "150.50")
    private BigDecimal valor;

    @Schema(description = "Status do pagamento", example = "APROVADO")
    private StatusPagamento status;

    @Schema(description = "Código da transação", example = "TXNABC123456")
    private String codigoTransacao;

    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data de confirmação")
    private LocalDateTime dataConfirmacao;

    @Schema(description = "Observações")
    private String observacao;

    @Schema(description = "Número de parcelas", example = "3")
    private Integer numeroParcelas;

    @Schema(description = "Bandeira do cartão", example = "VISA")
    private String bandeiraCartao;

    @Schema(description = "Últimos 4 dígitos do cartão", example = "1234")
    private String ultimosDigitosCartao;
}