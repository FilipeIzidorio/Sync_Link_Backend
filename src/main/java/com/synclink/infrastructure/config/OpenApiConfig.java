package com.synclink.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sync Link - Sistema de Gestão para Restaurantes",
                version = "1.0.0",
                description = """
                        API REST para gerenciamento completo de restaurantes:
                        • Controle de usuários e autenticação JWT  
                        • Gestão de produtos, mesas, pedidos e estoque  
                        • Sincronização em tempo real via WebSocket  
                        """
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081/sync-link",
                        description = "Servidor local de desenvolvimento"
                )
        },
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Cole aqui o token JWT obtido em `/auth/login`",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
