package com.synclink.application.dto;

import com.synclink.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para criação de usuário")
public class CreateUsuarioDTO {

    @Schema(description = "Nome do usuário", example = "João Silva")
    @NotBlank
    @Size(max = 100)
    private String nome;

    @Schema(description = "Email do usuário", example = "joao@synclink.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank
    @Size(min = 6)
    private String senha;

    @Schema(description = "Perfil do usuário", example = "GERENTE")
    @NotNull
    private PerfilUsuario perfil;

    // Construtores
    public CreateUsuarioDTO() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
}