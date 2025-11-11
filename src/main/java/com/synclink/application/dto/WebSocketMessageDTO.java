package com.synclink.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "DTO para mensagens WebSocket")
public class WebSocketMessageDTO {

    @Schema(description = "Tipo da mensagem", example = "PEDIDO_ATUALIZADO")
    private String tipo;

    @Schema(description = "Dados da mensagem")
    private Object dados;

    @Schema(description = "ID do usuário que enviou a mensagem")
    private Long usuarioId;

    @Schema(description = "Timestamp da mensagem")
    private LocalDateTime timestamp;

    @Schema(description = "Mesa relacionada (se aplicável)")
    private Long mesaId;

    @Schema(description = "ID da sessão WebSocket")
    private String sessionId;

    // Construtores
    public WebSocketMessageDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public WebSocketMessageDTO(String tipo, Object dados, Long usuarioId) {
        this();
        this.tipo = tipo;
        this.dados = dados;
        this.usuarioId = usuarioId;
    }

    public WebSocketMessageDTO(String tipo, Object dados, Long usuarioId, Long mesaId) {
        this(tipo, dados, usuarioId);
        this.mesaId = mesaId;
    }

    public WebSocketMessageDTO(String tipo, Object dados, Long usuarioId, Long mesaId, String sessionId) {
        this(tipo, dados, usuarioId, mesaId);
        this.sessionId = sessionId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Object getDados() {
        return dados;
    }

    public void setDados(Object dados) {
        this.dados = dados;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}