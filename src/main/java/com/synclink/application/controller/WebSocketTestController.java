package com.synclink.application.controller;

import com.synclink.application.dto.WebSocketMessageDTO;
import com.synclink.application.service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ws-test")
@Tag(name = "WebSocket Test", description = "Endpoints para teste do WebSocket")
public class WebSocketTestController {

    private final WebSocketService webSocketService;

    public WebSocketTestController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @PostMapping("/broadcast")
    @Operation(summary = "Enviar mensagem broadcast", description = "Envia uma mensagem para todos os clientes conectados")
    public void enviarBroadcast(@RequestBody WebSocketMessageDTO mensagem) {
        webSocketService.enviarParaTodos("/topic/test", mensagem);
    }

    @PostMapping("/notificar-mesas")
    @Operation(summary = "Notificar atualização de mesas", description = "Notifica todos os clientes sobre atualização de mesas")
    public void notificarMesas(@RequestBody Object dados) {
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "TESTE_MESAS",
                dados,
                null
        );
        webSocketService.enviarParaTodos("/topic/mesas", mensagem);
    }

    @PostMapping("/notificar-pedidos")
    @Operation(summary = "Notificar atualização de pedidos", description = "Notifica todos os clientes sobre atualização de pedidos")
    public void notificarPedidos(@RequestBody Object dados) {
        WebSocketMessageDTO mensagem = new WebSocketMessageDTO(
                "TESTE_PEDIDOS",
                dados,
                null
        );
        webSocketService.enviarParaTodos("/topic/pedidos", mensagem);
    }
}