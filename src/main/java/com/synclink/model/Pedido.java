package com.synclink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "acrescimo", precision = 10, scale = 2)
    private BigDecimal acrescimo = BigDecimal.ZERO;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "valor_final", precision = 10, scale = 2)
    private BigDecimal valorFinal = BigDecimal.ZERO;

    private String observacao;

    @Column(name = "justificativa_acrescimo")
    private String justificativaAcrescimo;

    @Column(name = "justificativa_desconto")
    private String justificativaDesconto;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos = new ArrayList<>();

    // Construtores
    public Pedido() {}

    public Pedido(Mesa mesa, Usuario usuario, String observacao) {
        this.mesa = mesa;
        this.usuario = usuario;
        this.observacao = observacao;
    }

    public Pedido(Comanda comanda, Usuario usuario, String observacao) {
        this.comanda = comanda;
        this.mesa = comanda.getMesa();
        this.usuario = usuario;
        this.observacao = observacao;
    }

    // Métodos auxiliares (mantidos os existentes)
    public void calcularTotais() {
        // Calcula subtotal baseado nos itens
        this.subtotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcula total (subtotal + acréscimo - desconto)
        this.total = this.subtotal.add(acrescimo).subtract(desconto);

        // Valor final é o total (pode ser usado para taxas adicionais no futuro)
        this.valorFinal = this.total;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
        calcularTotais();
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
        calcularTotais();
    }

    public void aplicarAcrescimo(BigDecimal valor, String justificativa) {
        this.acrescimo = valor;
        this.justificativaAcrescimo = justificativa;
        calcularTotais();
    }

    public void aplicarDesconto(BigDecimal valor, String justificativa) {
        this.desconto = valor;
        this.justificativaDesconto = justificativa;
        calcularTotais();
    }

    public void fecharPedido() {
        this.status = StatusPedido.FECHADO;
        this.dataFechamento = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters (adicionar os novos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Comanda getComanda() { return comanda; }
    public void setComanda(Comanda comanda) { this.comanda = comanda; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getAcrescimo() { return acrescimo; }
    public void setAcrescimo(BigDecimal acrescimo) { this.acrescimo = acrescimo; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal valorFinal) { this.valorFinal = valorFinal; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getJustificativaAcrescimo() { return justificativaAcrescimo; }
    public void setJustificativaAcrescimo(String justificativaAcrescimo) { this.justificativaAcrescimo = justificativaAcrescimo; }

    public String getJustificativaDesconto() { return justificativaDesconto; }
    public void setJustificativaDesconto(String justificativaDesconto) { this.justificativaDesconto = justificativaDesconto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }
}