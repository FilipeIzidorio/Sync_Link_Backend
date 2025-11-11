package com.synclink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(name = "estoque_maximo")
    private Integer estoqueMaximo;

    @Column(precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();

    @Column(name = "data_validade")
    private LocalDateTime dataValidade;

    private String lote;

    // Construtores
    public Estoque() {}

    public Estoque(Produto produto, Integer quantidade, Integer estoqueMinimo, BigDecimal custoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.custoUnitario = custoUnitario;
    }

    // Métodos auxiliares
    public Boolean precisaRepor() {
        return quantidade <= estoqueMinimo;
    }

    public void adicionarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }

    public void removerQuantidade(Integer quantidade) {
        this.quantidade -= quantidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public Integer getEstoqueMaximo() { return estoqueMaximo; }
    public void setEstoqueMaximo(Integer estoqueMaximo) { this.estoqueMaximo = estoqueMaximo; }

    public BigDecimal getCustoUnitario() { return custoUnitario; }
    public void setCustoUnitario(BigDecimal custoUnitario) { this.custoUnitario = custoUnitario; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
}