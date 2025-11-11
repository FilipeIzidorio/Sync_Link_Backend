package com.synclink.model.enums;

public enum StatusPedido {
    ABERTO("Pedido aberto"),
    EM_PREPARO("Em preparo"),
    PRONTO("Pronto para entrega"),
    ENTREGUE("Entregue"),
    FECHADO("Fechado"),
    CANCELADO("Cancelado");

    private final String descricao;
    StatusPedido(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}

