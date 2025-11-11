package com.synclink.application.controller;

import com.synclink.application.dto.auth.*;
import com.synclink.application.service.AuthService;
import com.synclink.domain.repository.UsuarioRepository;
import com.synclink.infrastructure.security.JwtService;
import com.synclink.model.PerfilUsuario;
import com.synclink.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador responsável pela autenticação e registro de usuários.
 * Inclui endpoints para signup, login e obtenção de informações do usuário autenticado.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro com JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;


    // ============================================================
    // 🔹 REGISTRO (SIGNUP)
    // ============================================================
    @PostMapping("/signup")
    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Cria um novo usuário no sistema e retorna um token JWT de autenticação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("erro", "E-mail já cadastrado."));
            }

            PerfilUsuario perfil;
            try {
                perfil = PerfilUsuario.valueOf(request.getPerfil().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Perfil inválido."));
            }

            Usuario usuario = new Usuario(
                    request.getNome(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getSenha()),
                    perfil
            );

            usuarioRepository.save(usuario);
            String token = jwtService.generateToken(usuario.getEmail());

            AuthResponse response = new AuthResponse(
                    token,
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil().name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Erro ao cadastrar usuário", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Erro ao cadastrar usuário: " + e.getMessage()));
        }
    }

    // ============================================================
    // 🔹 LOGIN
    // ============================================================
    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário com e-mail e senha e retorna um token JWT válido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request, authenticationManager);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.warn("Falha ao autenticar usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Credenciais inválidas"));
        }
    }

    // ============================================================
    // 🔹 USUÁRIO AUTENTICADO
    // ============================================================
    @GetMapping("/me")
    @Operation(
            summary = "Ver dados do usuário autenticado",
            description = "Retorna as informações do usuário logado com base no token JWT enviado no cabeçalho Authorization."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente")
    })
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Token JWT ausente ou inválido."));
            }

            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nome", usuario.getNome(),
                    "email", usuario.getEmail(),
                    "perfil", usuario.getPerfil()
            ));

        } catch (Exception e) {
            log.error("Erro ao obter usuário autenticado", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Token inválido ou expirado."));
        }
    }

    // ============================================================
    // 🔹 RENOVAÇÃO DE TOKEN (opcional)
    // ============================================================
    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar token JWT",
            description = "Gera um novo token JWT válido com base no token atual, se ainda for válido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Token JWT ausente ou inválido."));
            }

            String oldToken = authHeader.substring(7);
            String email = jwtService.extractUsername(oldToken);

            if (!jwtService.isTokenValid(oldToken, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Token expirado ou inválido."));
            }

            String newToken = jwtService.generateToken(email);
            TokenResponse response = TokenResponse.builder()
                    .token(newToken)
                    .tipo("Bearer")
                    .expiresInSeconds(jwtService.getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao renovar token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Não foi possível renovar o token."));
        }
    }
}
