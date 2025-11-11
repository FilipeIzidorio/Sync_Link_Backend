package com.synclink.application.controller;

import com.synclink.application.dto.*;
import com.synclink.application.service.AuthService;
import com.synclink.application.service.PedidoService;
import com.synclink.infrastructure.security.JwtService;
import com.synclink.model.StatusPedido;
import com.synclink.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos e vendas")
public class PedidoController {

    private final PedidoService pedidoService;
    private final JwtService jwtService;
    private final AuthService authService;

    // ==============================
    // CRUD BÁSICO
    // ==============================
    @GetMapping
    @Operation(summary = "Listar todos os pedidos")
    public ResponseEntity<List<PedidoDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar um novo pedido")
    public ResponseEntity<PedidoDTO> create(@Valid @RequestBody CreatePedidoDTO dto,
                                            @RequestHeader("Authorization") String token) {
        String email = jwtService.extractUsername(token.substring(7));
        Usuario usuario = (Usuario) authService.loadUserByUsername(email);

        PedidoDTO pedido = pedidoService.create(dto, usuario.getId());
        URI uri = URI.create("/api/pedidos/" + pedido.getId());
        return ResponseEntity.created(uri).body(pedido);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==============================
    // ITENS DO PEDIDO
    // ==============================
    @PostMapping("/{id}/itens")
    @Operation(summary = "Adicionar item ao pedido")
    public ResponseEntity<PedidoDTO> adicionarItem(@PathVariable Long id,
                                                   @Valid @RequestBody AdicionarItemPedidoDTO itemDTO) {
        return ResponseEntity.ok(pedidoService.adicionarItem(id, itemDTO));
    }

    @DeleteMapping("/{pedidoId}/itens/{itemId}")
    @Operation(summary = "Remover item do pedido")
    public ResponseEntity<PedidoDTO> removerItem(@PathVariable Long pedidoId, @PathVariable Long itemId) {
        return ResponseEntity.ok(pedidoService.removerItem(pedidoId, itemId));
    }

    @PatchMapping("/{id}/quantidade-item/{itemId}")
    @Operation(summary = "Atualizar quantidade de item no pedido")
    public ResponseEntity<PedidoDTO> atualizarQuantidade(@PathVariable Long id,
                                                         @PathVariable Long itemId,
                                                         @RequestParam Integer quantidade) {
        return ResponseEntity.ok(pedidoService.atualizarQuantidadeItem(id, itemId, quantidade));
    }

    // ==============================
    // FLUXO DO PEDIDO
    // ==============================
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<PedidoDTO> cancelar(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, motivo));
    }

    @PostMapping("/{id}/fechar")
    @Operation(summary = "Fechar pedido (antes da venda)")
    public ResponseEntity<PedidoDTO> fecharPedido(@PathVariable Long id,
                                                  @Valid @RequestBody FecharPedidoDTO dto) {
        return ResponseEntity.ok(pedidoService.fecharPedido(id, dto));
    }

    // ==============================
    // FINANCEIRO
    // ==============================
    @PostMapping("/{id}/finalizar-venda")
    @Operation(summary = "Finalizar venda com pagamento")
    public ResponseEntity<PedidoFinalizadoDTO> finalizarVenda(@PathVariable Long id,
                                                              @Valid @RequestBody FinalizarVendaDTO dto) {
        return ResponseEntity.ok(pedidoService.finalizarVenda(id, dto));
    }

    @GetMapping("/{id}/troco")
    @Operation(summary = "Calcular troco do pedido")
    public ResponseEntity<BigDecimal> calcularTroco(@PathVariable Long id, @RequestParam BigDecimal valorPago) {
        return ResponseEntity.ok(pedidoService.calcularTroco(id, valorPago));
    }

    // ==============================
    // CONSULTAS
    // ==============================
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos por status")
    public ResponseEntity<List<PedidoDTO>> porStatus(@PathVariable StatusPedido status) {
        return ResponseEntity.ok(pedidoService.findByStatus(status));
    }

    @GetMapping("/mesa/{mesaId}")
    @Operation(summary = "Listar pedidos por mesa")
    public ResponseEntity<List<PedidoDTO>> porMesa(@PathVariable Long mesaId) {
        return ResponseEntity.ok(pedidoService.findByMesaId(mesaId));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar pedidos ativos (não fechados)")
    public ResponseEntity<List<PedidoDTO>> ativos() {
        return ResponseEntity.ok(pedidoService.findPedidosAtivos());
    }

    @GetMapping("/cozinha")
    @Operation(summary = "Listar pedidos visíveis pela cozinha")
    public ResponseEntity<List<PedidoDTO>> cozinha() {
        return ResponseEntity.ok(pedidoService.findPedidosCozinha());
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas de vendas por período")
    public ResponseEntity<EstatisticasPedidosDTO> estatisticas(@RequestParam String dataInicio,
                                                               @RequestParam String dataFim) {
        return ResponseEntity.ok(pedidoService.obterEstatisticas(dataInicio, dataFim));
    }
}
