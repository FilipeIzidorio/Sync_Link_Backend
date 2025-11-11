package com.synclink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mesas")
@Data
@Builder
@AllArgsConstructor
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMesa status = StatusMesa.LIVRE;

    private String descricao;

    @OneToMany(mappedBy = "mesa")
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "mesa")
    private List<Comanda> comandas = new ArrayList<>();

    // Método para obter pedido ativo (não fechado)
    public Pedido getPedidoAtivo() {
        return pedidos.stream()
                .filter(pedido -> pedido.getStatus() != StatusPedido.FECHADO &&
                        pedido.getStatus() != StatusPedido.CANCELADO)
                .findFirst()
                .orElse(null);
    }

    // Método para verificar se tem pedido ativo
    public boolean temPedidoAtivo() {
        return getPedidoAtivo() != null;
    }

    // Método para obter comanda ativa
    public Comanda getComandaAtiva() {
        return comandas.stream()
                .filter(comanda -> comanda.getStatus() == StatusComanda.ABERTA)
                .findFirst()
                .orElse(null);
    }

    // Construtores
    public Mesa() {}

    public Mesa(Integer numero, String descricao) {
        this.numero = numero;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    public List<Comanda> getComandas() { return comandas; }
    public void setComandas(List<Comanda> comandas) { this.comandas = comandas; }
}