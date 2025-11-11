package com.synclink.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para autenticação de usuário")
public class AuthRequest {

    @Schema(description = "Email do usuário", example = "admin@synclink.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}