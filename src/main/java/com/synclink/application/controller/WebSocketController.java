package com.synclink.application.controller;

import com.synclink.application.dto.WebSocketMessageDTO;
import com.synclink.application.service.AuthService;
import com.synclink.application.service.WebSocketService;
import com.synclink.model.TiposMensagem;
import com.synclink.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket", description = "Endpoints para comunicação em tempo real via WebSocket")
public class WebSocketController {

    private final WebSocketService webSocketService;
    private final AuthService authService;

    // ============================================================
    // 🔹 CONEXÃO E DESCONEXÃO DE CLIENTES
    // ============================================================

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // Extrair informações do usuário do header (se disponível)
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";

        log.info("🔗 Cliente WebSocket conectado: SessionID: {}, Usuário: {}", sessionId, username);

        // Enviar mensagem de boas-vindas
        Map<String, Object> conexaoInfo = new HashMap<>();
        conexaoInfo.put("sessionId", sessionId);
        conexaoInfo.put("usuario", username);
        conexaoInfo.put("status", "conectado");
        conexaoInfo.put("timestamp", LocalDateTime.now().toString());

        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "CONEXAO_ESTABELECIDA",
                conexaoInfo,
                null
        );

        webSocketService.enviarParaUsuario(username, "/topic/connection", mensagem);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";

        log.info("🔌 Cliente WebSocket desconectado: SessionID: {}, Usuário: {}", sessionId, username);

        // Notificar desconexão
        Map<String, Object> desconexaoInfo = new HashMap<>();
        desconexaoInfo.put("sessionId", sessionId);
        desconexaoInfo.put("usuario", username);
        desconexaoInfo.put("status", "desconectado");
        desconexaoInfo.put("timestamp", LocalDateTime.now().toString());

        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "CONEXAO_ENCERRADA",
                desconexaoInfo,
                null
        );

        webSocketService.enviarParaTodos("/topic/connection", mensagem);
    }

    // ============================================================
    // 🔹 MENSAGENS GLOBAIS E BROADCAST
    // ============================================================

    @MessageMapping("/chat.global")
    @Operation(summary = "Enviar mensagem global de chat")
    public void enviarMensagemGlobal(@Payload WebSocketMessageDTO mensagem,
                                     SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            mensagem.setUsuarioId(obterUsuarioId(usuario));
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("💬 Mensagem global de {}: {}", usuario, mensagem.getTipo());

            webSocketService.enviarParaTodos("/topic/chat.global", mensagem);

        } catch (Exception e) {
            log.error("Erro ao processar mensagem global", e);
        }
    }

    @MessageMapping("/notificacao.geral")
    @Operation(summary = "Enviar notificação geral")
    public void enviarNotificacaoGeral(@Payload WebSocketMessageDTO mensagem,
                                       SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            mensagem.setUsuarioId(obterUsuarioId(usuario));
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("🔔 Notificação geral de {}: {}", usuario, mensagem.getTipo());

            webSocketService.enviarParaTodos("/topic/notificacao.geral", mensagem);

        } catch (Exception e) {
            log.error("Erro ao processar notificação geral", e);
        }
    }

    // ============================================================
    // 🔹 MENSAGENS ESPECÍFICAS POR MÓDULO
    // ============================================================

    @MessageMapping("/pedidos.acoes")
    @Operation(summary = "Ações relacionadas a pedidos")
    public void processarAcaoPedido(@Payload WebSocketMessageDTO mensagem,
                                    SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            Long usuarioId = obterUsuarioId(usuario);
            mensagem.setUsuarioId(usuarioId);
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("📦 Ação em pedido por {}: {}", usuario, mensagem.getTipo());

            // Processar diferentes tipos de ações de pedido
            switch (mensagem.getTipo()) {
                case "PEDIDO_SOLICITAR_ATUALIZACAO":
                    webSocketService.enviarParaUsuario(usuario, "/queue/pedidos.atualizacao", mensagem);
                    break;
                case "PEDIDO_SOLICITAR_DETALHES":
                    webSocketService.enviarParaUsuario(usuario, "/queue/pedidos.detalhes", mensagem);
                    break;
                default:
                    webSocketService.enviarParaTodos("/topic/pedidos", mensagem);
            }

        } catch (Exception e) {
            log.error("Erro ao processar ação de pedido", e);
        }
    }

    @MessageMapping("/mesas.acoes")
    @Operation(summary = "Ações relacionadas a mesas")
    public void processarAcaoMesa(@Payload WebSocketMessageDTO mensagem,
                                  SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            Long usuarioId = obterUsuarioId(usuario);
            mensagem.setUsuarioId(usuarioId);
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("🪑 Ação em mesa por {}: {}", usuario, mensagem.getTipo());

            webSocketService.enviarParaTodos("/topic/mesas", mensagem);

        } catch (Exception e) {
            log.error("Erro ao processar ação de mesa", e);
        }
    }

    @MessageMapping("/cozinha.acoes")
    @Operation(summary = "Ações relacionadas à cozinha")
    public void processarAcaoCozinha(@Payload WebSocketMessageDTO mensagem,
                                     SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            Long usuarioId = obterUsuarioId(usuario);
            mensagem.setUsuarioId(usuarioId);
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("👨‍🍳 Ação na cozinha por {}: {}", usuario, mensagem.getTipo());

            switch (mensagem.getTipo()) {
                case "PEDIDO_INICIAR_PREPARO":
                case "PEDIDO_FINALIZAR_PREPARO":
                case "PEDIDO_MARCAR_PRONTO":
                    webSocketService.enviarParaTodos("/topic/cozinha", mensagem);
                    // Notificar também os garçons
                    webSocketService.enviarParaTodos("/topic/garcom", mensagem);
                    break;
                case "COZINHA_SOLICITAR_PEDIDOS":
                    webSocketService.enviarParaUsuario(usuario, "/queue/cozinha.pedidos", mensagem);
                    break;
                default:
                    webSocketService.enviarParaTodos("/topic/cozinha", mensagem);
            }

        } catch (Exception e) {
            log.error("Erro ao processar ação da cozinha", e);
        }
    }

    @MessageMapping("/caixa.acoes")
    @Operation(summary = "Ações relacionadas ao caixa")
    public void processarAcaoCaixa(@Payload WebSocketMessageDTO mensagem,
                                   SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);
            Long usuarioId = obterUsuarioId(usuario);
            mensagem.setUsuarioId(usuarioId);
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("💰 Ação no caixa por {}: {}", usuario, mensagem.getTipo());

            webSocketService.enviarParaTodos("/topic/caixa", mensagem);

        } catch (Exception e) {
            log.error("Erro ao processar ação do caixa", e);
        }
    }

    // ============================================================
    // 🔹 MENSAGENS PRIVADAS ENTRE USUÁRIOS
    // ============================================================

    @MessageMapping("/chat.privado")
    @Operation(summary = "Enviar mensagem privada")
    public void enviarMensagemPrivada(@Payload WebSocketMessageDTO mensagem,
                                      SimpMessageHeaderAccessor headerAccessor) {
        try {
            String remetente = obterUsuarioDoHeader(headerAccessor);
            Long remetenteId = obterUsuarioId(remetente);

            // O destinatário deve estar no payload
            String destinatario = (String) mensagem.getDados();
            if (destinatario == null) {
                log.warn("Tentativa de enviar mensagem privada sem destinatário");
                return;
            }

            mensagem.setUsuarioId(remetenteId);
            mensagem.setTimestamp(LocalDateTime.now());

            log.info("📨 Mensagem privada de {} para {}", remetente, destinatario);

            // Enviar para o destinatário específico
            webSocketService.enviarParaUsuario(destinatario, "/queue/chat.privado", mensagem);

            // Enviar confirmação para o remetente
            Map<String, Object> confirmacao = new HashMap<>();
            confirmacao.put("mensagemId", mensagem.hashCode());
            confirmacao.put("destinatario", destinatario);
            confirmacao.put("status", "entregue");
            confirmacao.put("timestamp", LocalDateTime.now().toString());

            WebSocketMessageDTO confirmacaoMsg = new WebSocketMessageDTO(
                    "MENSAGEM_ENTREGUE",
                    confirmacao,
                    remetenteId
            );

            webSocketService.enviarParaUsuario(remetente, "/queue/chat.confirmacao", confirmacaoMsg);

        } catch (Exception e) {
            log.error("Erro ao processar mensagem privada", e);
        }
    }

    // ============================================================
    // 🔹 SUBSCRIÇÕES PARA ATUALIZAÇÕES AUTOMÁTICAS
    // ============================================================

    @SubscribeMapping("/topic/mesas")
    @Operation(summary = "Subscribe para atualizações de mesas")
    public WebSocketMessageDTO subscribeMesas(Principal principal) {
        String usuario = principal != null ? principal.getName() : "Anonymous";

        Map<String, Object> dados = new HashMap<>();
        dados.put("mensagem", "Conectado às atualizações de mesas");
        dados.put("usuario", usuario);
        dados.put("timestamp", LocalDateTime.now().toString());

        log.info("📡 Usuário {} subscreveu para atualizações de mesas", usuario);

        return new WebSocketMessageDTO(
                "SUBSCRICAO_MESAS_ATIVA",
                dados,
                obterUsuarioId(usuario)
        );
    }

    @SubscribeMapping("/topic/pedidos")
    @Operation(summary = "Subscribe para atualizações de pedidos")
    public WebSocketMessageDTO subscribePedidos(Principal principal) {
        String usuario = principal != null ? principal.getName() : "Anonymous";

        Map<String, Object> dados = new HashMap<>();
        dados.put("mensagem", "Conectado às atualizações de pedidos");
        dados.put("usuario", usuario);
        dados.put("timestamp", LocalDateTime.now().toString());

        log.info("📡 Usuário {} subscreveu para atualizações de pedidos", usuario);

        return new WebSocketMessageDTO(
                "SUBSCRICAO_PEDIDOS_ATIVA",
                dados,
                obterUsuarioId(usuario)
        );
    }

    @SubscribeMapping("/topic/cozinha")
    @Operation(summary = "Subscribe para atualizações da cozinha")
    public WebSocketMessageDTO subscribeCozinha(Principal principal) {
        String usuario = principal != null ? principal.getName() : "Anonymous";

        Map<String, Object> dados = new HashMap<>();
        dados.put("mensagem", "Conectado às atualizações da cozinha");
        dados.put("usuario", usuario);
        dados.put("timestamp", LocalDateTime.now().toString());

        log.info("📡 Usuário {} subscreveu para atualizações da cozinha", usuario);

        return new WebSocketMessageDTO(
                "SUBSCRICAO_COZINHA_ATIVA",
                dados,
                obterUsuarioId(usuario)
        );
    }

    @SubscribeMapping("/user/queue/notificacoes")
    @Operation(summary = "Subscribe para notificações pessoais")
    public WebSocketMessageDTO subscribeNotificacoesPessoais(Principal principal) {
        String usuario = principal != null ? principal.getName() : "Anonymous";

        Map<String, Object> dados = new HashMap<>();
        dados.put("mensagem", "Conectado às notificações pessoais");
        dados.put("usuario", usuario);
        dados.put("timestamp", LocalDateTime.now().toString());

        log.info("📡 Usuário {} subscreveu para notificações pessoais", usuario);

        return new WebSocketMessageDTO(
                "SUBSCRICAO_NOTIFICACOES_ATIVA",
                dados,
                obterUsuarioId(usuario)
        );
    }

    // ============================================================
    // 🔹 MÉTODOS AUXILIARES
    // ============================================================

    private String obterUsuarioDoHeader(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getUser() != null) {
            return headerAccessor.getUser().getName();
        }
        return "Anonymous";
    }

    private Long obterUsuarioId(String username) {
        try {
            if (!"Anonymous".equals(username)) {
                Usuario usuario = (Usuario) authService.loadUserByUsername(username);
                return usuario != null ? usuario.getId() : null;
            }
        } catch (Exception e) {
            log.warn("Não foi possível obter ID do usuário: {}", username);
        }
        return null;
    }

    // ============================================================
    // 🔹 MÉTODOS PARA HEALTH CHECK E STATUS
    // ============================================================

    @MessageMapping("/system.ping")
    @Operation(summary = "Health check do WebSocket")
    public void processarPing(@Payload WebSocketMessageDTO mensagem,
                              SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);

            Map<String, Object> pongData = new HashMap<>();
            pongData.put("timestamp", LocalDateTime.now().toString());
            pongData.put("serverTime", System.currentTimeMillis());
            pongData.put("status", "online");

            WebSocketMessageDTO pong = new WebSocketMessageDTO(
                    "SYSTEM_PONG",
                    pongData,
                    obterUsuarioId(usuario)
            );

            webSocketService.enviarParaUsuario(usuario, "/queue/system.status", pong);

        } catch (Exception e) {
            log.error("Erro ao processar ping", e);
        }
    }

    @MessageMapping("/system.status")
    @Operation(summary = "Solicitar status do sistema")
    public void obterStatusSistema(@Payload WebSocketMessageDTO mensagem,
                                   SimpMessageHeaderAccessor headerAccessor) {
        try {
            String usuario = obterUsuarioDoHeader(headerAccessor);

            Map<String, Object> statusData = new HashMap<>();
            statusData.put("timestamp", LocalDateTime.now().toString());
            statusData.put("serverTime", System.currentTimeMillis());
            statusData.put("status", "operacional");
            statusData.put("version", "1.0.0");
            statusData.put("activeConnections", "N/A"); // Poderia ser obtido de um serviço de monitoramento

            WebSocketMessageDTO status = new WebSocketMessageDTO(
                    "SYSTEM_STATUS",
                    statusData,
                    obterUsuarioId(usuario)
            );

            webSocketService.enviarParaUsuario(usuario, "/queue/system.status", status);

        } catch (Exception e) {
            log.error("Erro ao processar solicitação de status", e);
        }
    }
}