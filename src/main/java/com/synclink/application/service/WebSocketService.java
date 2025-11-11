package com.synclink.application.service;

import com.synclink.model.TiposMensagem;
import com.synclink.application.dto.WebSocketMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AuthService authService;

    // ============================================================
    // 🔹 MÉTODOS BÁSICOS DE ENVIO
    // ============================================================

    public void enviarParaTodos(String destino, WebSocketMessageDTO mensagem) {
        try {
            messagingTemplate.convertAndSend(destino, mensagem);
            log.debug("📤 Mensagem enviada para {}: {}", destino, mensagem.getTipo());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar mensagem para {}: {}", destino, e.getMessage());
        }
    }

    public void enviarParaUsuario(String usuario, String destino, WebSocketMessageDTO mensagem) {
        try {
            messagingTemplate.convertAndSendToUser(usuario, destino, mensagem);
            log.debug("📤 Mensagem enviada para usuário {} em {}: {}", usuario, destino, mensagem.getTipo());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar mensagem para usuário {}: {}", usuario, e.getMessage());
        }
    }

    // ============================================================
    // 🔹 NOTIFICAÇÕES ESPECÍFICAS DO SISTEMA
    // ============================================================

    public void notificarPedidoCriado(Object pedidoDTO, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.PEDIDO_CRIADO,
                pedidoDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/pedidos", mensagem);
        enviarParaTodos("/topic/mesas", mensagem);
        log.info("🚀 Notificação: Pedido criado - Mesa {}", mesaId);
    }

    public void notificarPedidoAtualizado(Object pedidoDTO, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.PEDIDO_ATUALIZADO,
                pedidoDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/pedidos", mensagem);
        enviarParaTodos("/topic/mesa." + mesaId, mensagem);
        log.info("🔄 Notificação: Pedido atualizado - Mesa {}", mesaId);
    }

    public void notificarItemAdicionado(Object itemDTO, Long pedidoId, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.ITEM_ADICIONADO,
                itemDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/pedidos", mensagem);
        enviarParaTodos("/topic/mesa." + mesaId, mensagem);
        enviarParaTodos("/topic/cozinha", mensagem);
        log.info("➕ Notificação: Item adicionado - Pedido {}", pedidoId);
    }

    public void notificarPedidoFechado(Object pedidoDTO, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.PEDIDO_FECHADO,
                pedidoDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/pedidos", mensagem);
        enviarParaTodos("/topic/mesas", mensagem);
        enviarParaTodos("/topic/caixa", mensagem);
        log.info("💰 Notificação: Pedido fechado - Mesa {}", mesaId);
    }

    public void notificarMesaAtualizada(Object mesaDTO) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.MESA_ATUALIZADA,
                mesaDTO,
                usuarioId
        );
        enviarParaTodos("/topic/mesas", mensagem);
        log.info("🪑 Notificação: Mesa atualizada");
    }

    public void notificarCozinha(Object pedidoDTO) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                TiposMensagem.ACIONAMENTO_COZINHA,
                pedidoDTO,
                usuarioId
        );
        enviarParaTodos("/topic/cozinha", mensagem);
        log.info("👨‍🍳 Notificação: Acionamento cozinha");
    }

    public void notificarPagamentoProcessado(Object pagamentoDTO, Long pedidoId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "PAGAMENTO_PROCESSADO",
                pagamentoDTO,
                usuarioId
        );
        enviarParaTodos("/topic/pagamentos", mensagem);
        enviarParaTodos("/topic/caixa", mensagem);
        log.info("💳 Notificação: Pagamento processado - Pedido {}", pedidoId);
    }

    public void notificarComandaAberta(Object comandaDTO, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "COMANDA_ABERTA",
                comandaDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/comandas", mensagem);
        enviarParaTodos("/topic/mesas", mensagem);
        log.info("📋 Notificação: Comanda aberta - Mesa {}", mesaId);
    }

    public void notificarComandaFechada(Object comandaDTO, Long mesaId) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "COMANDA_FECHADA",
                comandaDTO,
                usuarioId,
                mesaId
        );
        enviarParaTodos("/topic/comandas", mensagem);
        enviarParaTodos("/topic/mesas", mensagem);
        enviarParaTodos("/topic/caixa", mensagem);
        log.info("📋 Notificação: Comanda fechada - Mesa {}", mesaId);
    }

    // ============================================================
    // 🔹 NOTIFICAÇÕES DE SISTEMA E ALERTAS
    // ============================================================

    public void notificarAlertaSistema(String tipoAlerta, String mensagem, String criticidade) {
        Long usuarioId = obterUsuarioIdAtual();

        Map<String, Object> alertaData = new HashMap<>();
        alertaData.put("tipo", tipoAlerta);
        alertaData.put("mensagem", mensagem);
        alertaData.put("criticidade", criticidade);
        alertaData.put("timestamp", java.time.LocalDateTime.now().toString());

        WebSocketMessageDTO alerta = new WebSocketMessageDTO(
                "ALERTA_SISTEMA",
                alertaData,
                usuarioId
        );

        enviarParaTodos("/topic/system.alertas", alerta);
        log.warn("🚨 Alerta do sistema: {} - {}", tipoAlerta, mensagem);
    }

    public void notificarEstoqueBaixo(Object estoqueDTO) {
        Long usuarioId = obterUsuarioIdAtual();
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "ESTOQUE_BAIXO",
                estoqueDTO,
                usuarioId
        );
        enviarParaTodos("/topic/estoque", mensagem);
        enviarParaTodos("/topic/gerente", mensagem);
        log.warn("📦 Notificação: Estoque baixo");
    }

    // ============================================================
    // 🔹 MÉTODOS AUXILIARES
    // ============================================================

    private Long obterUsuarioIdAtual() {
        try {
            var usuario = authService.getCurrentUser();
            return usuario != null ? usuario.getId() : null;
        } catch (Exception e) {
            log.debug("Não foi possível obter ID do usuário atual: {}", e.getMessage());
            return null;
        }
    }

    // ============================================================
    // 🔹 MÉTODOS PARA ENVIO EM LOTE
    // ============================================================

    public void enviarParaGrupo(String grupo, String destino, WebSocketMessageDTO mensagem) {
        try {
            // Para grupos específicos (ex: todos os garçons, todos da cozinha)
            enviarParaTodos("/topic/grupo." + grupo + "." + destino, mensagem);
            log.debug("📤 Mensagem enviada para grupo {} em {}: {}", grupo, destino, mensagem.getTipo());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar mensagem para grupo {}: {}", grupo, e.getMessage());
        }
    }

    public void enviarParaPerfil(String perfil, String destino, WebSocketMessageDTO mensagem) {
        try {
            // Para perfis específicos (ex: todos os usuários com perfil GARCOM)
            enviarParaTodos("/topic/perfil." + perfil + "." + destino, mensagem);
            log.debug("📤 Mensagem enviada para perfil {} em {}: {}", perfil, destino, mensagem.getTipo());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar mensagem para perfil {}: {}", perfil, e.getMessage());
        }
    }
}