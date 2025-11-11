package com.synclink.application.dto;

import com.synclink.model.enums.FormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para finalizar uma venda e registrar pagamento")
public class FinalizarVendaDTO {

    @Schema(description = "Valor pago pelo cliente", example = "100.00")
    private BigDecimal valorPago;

    @Schema(description = "Forma de pagamento", example = "DINHEIRO")
    private FormaPagamento formaPagamento;

    @Schema(description = "Código de transação (para cartões/pix)")
    private String codigoTransacao;

    @Schema(description = "Nome do cliente")
    private String nomeCliente;

    @Schema(description = "Documento (CPF/CNPJ)")
    private String documentoCliente;

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }
}
