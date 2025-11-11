package com.synclink.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para resposta de autenticação")
public class AuthResponse {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String tipo = "Bearer";

    @Schema(description = "ID do usuário", example = "1")
    private Long usuarioId;

    @Schema(description = "Nome do usuário", example = "Administrador")
    private String nome;

    @Schema(description = "Email do usuário", example = "admin@synclink.com")
    private String email;

    @Schema(description = "Perfil do usuário", example = "ADMIN")
    private String perfil;

    // Construtores
    public AuthResponse() {}

    public AuthResponse(String token, Long usuarioId, String nome, String email, String perfil) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}