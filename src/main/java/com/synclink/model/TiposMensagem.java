package com.synclink.model;

public class TiposMensagem {
    // Mensagens de Sistema
    public static final String CONEXAO_ESTABELECIDA = "CONEXAO_ESTABELECIDA";
    public static final String CONEXAO_ENCERRADA = "CONEXAO_ENCERRADA";
    public static final String SYSTEM_PONG = "SYSTEM_PONG";
    public static final String SYSTEM_STATUS = "SYSTEM_STATUS";
    public static final String ALERTA_SISTEMA = "ALERTA_SISTEMA";

    // Mensagens de Pedidos
    public static final String PEDIDO_CRIADO = "PEDIDO_CRIADO";
    public static final String PEDIDO_ATUALIZADO = "PEDIDO_ATUALIZADO";
    public static final String ITEM_ADICIONADO = "ITEM_ADICIONADO";
    public static final String ITEM_REMOVIDO = "ITEM_REMOVIDO";
    public static final String PEDIDO_FECHADO = "PEDIDO_FECHADO";
    public static final String PEDIDO_CANCELADO = "PEDIDO_CANCELADO";
    public static final String PEDIDO_SOLICITAR_ATUALIZACAO = "PEDIDO_SOLICITAR_ATUALIZACAO";
    public static final String PEDIDO_SOLICITAR_DETALHES = "PEDIDO_SOLICITAR_DETALHES";

    // Mensagens de Mesas
    public static final String MESA_ATUALIZADA = "MESA_ATUALIZADA";
    public static final String MESA_OCUPADA = "MESA_OCUPADA";
    public static final String MESA_LIVRE = "MESA_LIVRE";


    // Mensagens de Comandas
    public static final String COMANDA_ABERTA = "COMANDA_ABERTA";
    public static final String COMANDA_FECHADA = "COMANDA_FECHADA";
    public static final String COMANDA_CANCELADA = "COMANDA_CANCELADA";

    // Mensagens de Pagamento
    public static final String PAGAMENTO_PROCESSADO = "PAGAMENTO_PROCESSADO";
    public static final String PAGAMENTO_ESTORNADO = "PAGAMENTO_ESTORNADO";

    // Mensagens de Estoque
    public static final String ESTOQUE_BAIXO = "ESTOQUE_BAIXO";
    public static final String ESTOQUE_ATUALIZADO = "ESTOQUE_ATUALIZADO";

    // Mensagens de Chat
    public static final String MENSAGEM_GLOBAL = "MENSAGEM_GLOBAL";
    public static final String MENSAGEM_PRIVADA = "MENSAGEM_PRIVADA";
    public static final String MENSAGEM_ENTREGUE = "MENSAGEM_ENTREGUE";

    // Mensagens de Notificação
    public static final String NOTIFICACAO_GERAL = "NOTIFICACAO_GERAL";

    // Mensagens de Subscrição
    public static final String SUBSCRICAO_MESAS_ATIVA = "SUBSCRICAO_MESAS_ATIVA";
    public static final String SUBSCRICAO_PEDIDOS_ATIVA = "SUBSCRICAO_PEDIDOS_ATIVA";
    public static final String SUBSCRICAO_NOTIFICACOES_ATIVA = "SUBSCRICAO_NOTIFICACOES_ATIVA";
}