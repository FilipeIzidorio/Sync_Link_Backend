package com.synclink.application.controller;

import com.synclink.application.dto.ComandaDTO;
import com.synclink.application.service.ComandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
@Tag(name = "Comandas", description = "Endpoints de gerenciamento de comandas")
public class ComandaController {

    private final ComandaService comandaService;

    @PostMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    @Operation(summary = "Abrir comanda para uma mesa específica")
    public ResponseEntity<ComandaDTO> abrirComanda(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.abrirComanda(mesaId));
    }

    @PostMapping("/{id}/fechar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Fechar uma comanda aberta")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    @Operation(summary = "Buscar comanda por código")
    public ResponseEntity<ComandaDTO> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(comandaService.findByCodigo(codigo));
    }

    @GetMapping("/mesa/{mesaId}")
    @Operation(summary = "Listar comandas de uma mesa específica")
    public ResponseEntity<List<ComandaDTO>> findByMesaId(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.findByMesaId(mesaId));
    }

    @GetMapping("/abertas")
    @Operation(summary = "Listar todas as comandas abertas")
    public ResponseEntity<List<ComandaDTO>> findComandasAbertas() {
        return ResponseEntity.ok(comandaService.findComandasAbertas());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar comandas por status")
    public ResponseEntity<List<ComandaDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(comandaService.findByStatus(status));
    }

    @PostMapping("/{comandaId}/pedidos/{pedidoId}")
    @Operation(summary = "Adicionar pedido à comanda")
    public ResponseEntity<ComandaDTO> adicionarPedido(
            @PathVariable Long comandaId,
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(comandaService.adicionarPedido(comandaId, pedidoId));
    }

    @DeleteMapping("/{comandaId}/pedidos/{pedidoId}")
    @Operation(summary = "Remover pedido da comanda")
    public ResponseEntity<ComandaDTO> removerPedido(
            @PathVariable Long comandaId,
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(comandaService.removerPedido(comandaId, pedidoId));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar comanda aberta")
    public ResponseEntity<ComandaDTO> cancelarComanda(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(comandaService.cancelarComanda(id, motivo));
    }
}
