package com.synclink.application.controller;

import com.synclink.application.dto.ComandaDTO;
import com.synclink.application.service.ComandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
@Tag(name = "Comandas", description = "Operações para gerenciamento de comandas")
@SecurityRequirement(name = "bearerAuth")
public class ComandaController {

    private final ComandaService comandaService;

    @PostMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    @Operation(summary = "Abrir comanda para mesa", description = "Abre uma nova comanda para a mesa especificada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comanda aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Mesa não disponível ou já existe comanda aberta"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada")
    })
    public ResponseEntity<ComandaDTO> abrirComanda(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.abrirComanda(mesaId));
    }

    @PostMapping("/{id}/fechar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM', 'CAIXA')")
    @Operation(summary = "Fechar comanda", description = "Fecha uma comanda aberta")
    public ResponseEntity<ComandaDTO> fecharComanda(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.fecharComanda(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM', 'CAIXA')")
    @Operation(summary = "Buscar comanda por ID")
    public ResponseEntity<ComandaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM', 'CAIXA')")
    @Operation(summary = "Buscar comanda por código")
    public ResponseEntity<ComandaDTO> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(comandaService.findByCodigo(codigo));
    }

    @GetMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM', 'CAIXA')")
    @Operation(summary = "Listar comandas por mesa")
    public ResponseEntity<List<ComandaDTO>> findByMesaId(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.findByMesaId(mesaId));
    }

    @GetMapping("/abertas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM', 'CAIXA')")
    @Operation(summary = "Listar comandas abertas")
    public ResponseEntity<List<ComandaDTO>> findComandasAbertas() {
        return ResponseEntity.ok(comandaService.findComandasAbertas());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar comandas por status")
    public ResponseEntity<List<ComandaDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(comandaService.findByStatus(status));
    }

    @PostMapping("/{comandaId}/pedidos/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    @Operation(summary = "Adicionar pedido à comanda")
    public ResponseEntity<ComandaDTO> adicionarPedido(
            @PathVariable Long comandaId,
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(comandaService.adicionarPedido(comandaId, pedidoId));
    }

    @DeleteMapping("/{comandaId}/pedidos/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    @Operation(summary = "Remover pedido da comanda")
    public ResponseEntity<ComandaDTO> removerPedido(
            @PathVariable Long comandaId,
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(comandaService.removerPedido(comandaId, pedidoId));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Cancelar comanda")
    public ResponseEntity<ComandaDTO> cancelarComanda(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(comandaService.cancelarComanda(id, motivo));
    }
}