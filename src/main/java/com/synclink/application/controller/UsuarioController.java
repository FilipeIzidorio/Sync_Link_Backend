package com.synclink.application.controller;

import com.synclink.application.dto.UsuarioDTO;
import com.synclink.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Operações para gerenciamento de usuários - Acesso restrito")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos os usuários", description = "Acesso restrito a ADMIN e GERENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Permissão insuficiente")
    })
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or (hasRole('GARCOM') and @usuarioService.isCurrentUser(#id))")
    @Operation(summary = "Buscar usuário por ID", description = "ADMIN e GERENTE podem buscar qualquer usuário. Usuários comuns só podem buscar seus próprios dados")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo usuário", description = "Acesso restrito exclusivamente a ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Apenas ADMIN pode criar usuários")
    })
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.create(usuarioDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('GERENTE', 'GARCOM', 'COZINHA', 'CAIXA') and @usuarioService.isCurrentUser(#id))")
    @Operation(summary = "Atualizar usuário", description = "ADMIN pode atualizar qualquer usuário. Usuários comuns só podem atualizar seus próprios dados")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir usuário", description = "Acesso restrito exclusivamente a ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Apenas ADMIN pode excluir usuários")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativar usuário", description = "Acesso restrito a ADMIN")
    public ResponseEntity<UsuarioDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.ativar(id));
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inativar usuário", description = "Acesso restrito a ADMIN")
    public ResponseEntity<UsuarioDTO> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.inativar(id));
    }

    @GetMapping("/perfil/{perfil}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar usuários por perfil", description = "Acesso restrito a ADMIN e GERENTE")
    public ResponseEntity<List<UsuarioDTO>> findByPerfil(@PathVariable String perfil) {
        return ResponseEntity.ok(usuarioService.findByPerfil(perfil));
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar usuários ativos", description = "Acesso restrito a ADMIN e GERENTE")
    public ResponseEntity<List<UsuarioDTO>> findAtivos() {
        return ResponseEntity.ok(usuarioService.findByAtivo(true));
    }

    // 🔹 NOVO ENDPOINT - Usuário atual pode ver seus próprios dados
    @GetMapping("/meu-perfil")
    @Operation(summary = "Obter perfil do usuário atual", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<UsuarioDTO> getMeuPerfil() {
        return ResponseEntity.ok(usuarioService.getUsuarioAtual());
    }
}