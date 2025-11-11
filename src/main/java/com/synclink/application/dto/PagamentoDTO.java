package com.synclink.application.dto;

import com.synclink.model.enums.FormaPagamento;
import com.synclink.model.enums.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de transferência de dados de pagamento")
public class PagamentoDTO {

    private Long id;

    @Schema(description = "ID do pedido vinculado ao pagamento", example = "1")
    private Long pedidoId;

    @Schema(description = "Valor total do pedido", example = "150.00")
    private BigDecimal pedidoTotal;

    @Schema(description = "Forma de pagamento utilizada", example = "CARTAO_CREDITO")
    private FormaPagamento formaPagamento;

    @Schema(description = "Valor do pagamento realizado", example = "150.00")
    private BigDecimal valor;

    @Schema(description = "Status atual do pagamento", example = "APROVADO")
    private StatusPagamento status;

    @Schema(description = "Código da transação no sistema externo", example = "TXN-12345")
    private String codigoTransacao;

    @Schema(description = "Data de criação do pagamento")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data de confirmação do pagamento")
    private LocalDateTime dataConfirmacao;

    @Schema(description = "Observações adicionais")
    private String observacao;

    @Schema(description = "Número de parcelas", example = "2")
    private Integer numeroParcelas;

    @Schema(description = "Bandeira do cartão", example = "VISA")
    private String bandeiraCartao;

    @Schema(description = "Últimos 4 dígitos do cartão", example = "1234")
    private String ultimosDigitosCartao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getPedidoTotal() {
        return pedidoTotal;
    }

    public void setPedidoTotal(BigDecimal pedidoTotal) {
        this.pedidoTotal = pedidoTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(LocalDateTime dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public String getBandeiraCartao() {
        return bandeiraCartao;
    }

    public void setBandeiraCartao(String bandeiraCartao) {
        this.bandeiraCartao = bandeiraCartao;
    }

    public String getUltimosDigitosCartao() {
        return ultimosDigitosCartao;
    }

    public void setUltimosDigitosCartao(String ultimosDigitosCartao) {
        this.ultimosDigitosCartao = ultimosDigitosCartao;
    }
}
