package com.synclink.application.controller;

import com.synclink.application.dto.*;
import com.synclink.application.service.AuthService;
import com.synclink.application.service.PedidoService;
import com.synclink.infrastructure.security.JwtService;
import com.synclink.model.FormaPagamento;
import com.synclink.model.StatusPedido;
import com.synclink.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operações para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final JwtService jwtService;
    private final AuthService authService;

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
    @Operation(summary = "Criar novo pedido")
    public ResponseEntity<PedidoDTO> create(@Valid @RequestBody CreatePedidoDTO createPedidoDTO,
                                            @RequestHeader("Authorization") String token) {
        // Extrai o email do token JWT
        String email = jwtService.extractUsername(token.substring(7));

        // Busca o usuário pelo email para obter o ID real
        Usuario usuario = (Usuario) authService.loadUserByUsername(email);
        Long usuarioId = usuario.getId();

        return ResponseEntity.ok(pedidoService.create(createPedidoDTO, usuarioId));
    }

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

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status));
    }

    @PatchMapping("/{id}/acrescimo")
    @Operation(summary = "Aplicar acréscimo ao pedido")
    public ResponseEntity<PedidoDTO> aplicarAcrescimo(@PathVariable Long id,
                                                      @RequestParam BigDecimal valor,
                                                      @RequestParam(required = false) String justificativa) {
        return ResponseEntity.ok(pedidoService.aplicarAcrescimo(id, valor, justificativa));
    }

    @PatchMapping("/{id}/desconto")
    @Operation(summary = "Aplicar desconto ao pedido")
    public ResponseEntity<PedidoDTO> aplicarDesconto(@PathVariable Long id,
                                                     @RequestParam BigDecimal valor,
                                                     @RequestParam(required = false) String justificativa) {
        return ResponseEntity.ok(pedidoService.aplicarDesconto(id, valor, justificativa));
    }

    @PostMapping("/{id}/fechar")
    @Operation(summary = "Fechar pedido")
    public ResponseEntity<PedidoDTO> fecharPedido(@PathVariable Long id,
                                                  @Valid @RequestBody FecharPedidoDTO fecharPedidoDTO) {
        return ResponseEntity.ok(pedidoService.fecharPedido(id, fecharPedidoDTO));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos por status")
    public ResponseEntity<List<PedidoDTO>> findByStatus(@PathVariable StatusPedido status) {
        return ResponseEntity.ok(pedidoService.findByStatus(status));
    }

    @GetMapping("/mesa/{mesaId}")
    @Operation(summary = "Listar pedidos por mesa")
    public ResponseEntity<List<PedidoDTO>> findByMesaId(@PathVariable Long mesaId) {
        return ResponseEntity.ok(pedidoService.findByMesaId(mesaId));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar pedidos ativos")
    public ResponseEntity<List<PedidoDTO>> findPedidosAtivos() {
        return ResponseEntity.ok(pedidoService.findPedidosAtivos());
    }




    @PostMapping("/{id}/finalizar-venda")
    @Operation(summary = "Finalizar venda do pedido")
    public ResponseEntity<PedidoFinalizadoDTO> finalizarVenda(
            @PathVariable Long id,
            @Valid @RequestBody FinalizarVendaDTO finalizarVendaDTO) {
        return ResponseEntity.ok(pedidoService.finalizarVenda(id, finalizarVendaDTO));
    }

    @GetMapping("/{id}/troco")
    @Operation(summary = "Calcular troco do pedido")
    public ResponseEntity<BigDecimal> calcularTroco(
            @PathVariable Long id,
            @RequestParam BigDecimal valorPago) {
        return ResponseEntity.ok(pedidoService.calcularTroco(id, valorPago));
    }

    @PostMapping("/{id}/reabrir")
    @Operation(summary = "Reabrir pedido fechado")
    public ResponseEntity<PedidoDTO> reabrirPedido(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.reabrirPedido(id, motivo));
    }

    @PatchMapping("/{id}/quantidade-item/{itemId}")
    @Operation(summary = "Atualizar quantidade de item")
    public ResponseEntity<PedidoDTO> atualizarQuantidadeItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestParam Integer quantidade) {
        return ResponseEntity.ok(pedidoService.atualizarQuantidadeItem(id, itemId, quantidade));
    }

    @PostMapping("/{id}/mover-preparo")
    @Operation(summary = "Mover pedido para preparo")
    public ResponseEntity<PedidoDTO> moverParaPreparo(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.moverParaPreparo(id));
    }

    @PostMapping("/{id}/marcar-pronto")
    @Operation(summary = "Marcar pedido como pronto")
    public ResponseEntity<PedidoDTO> marcarComoPronto(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.marcarComoPronto(id));
    }

    @PostMapping("/{id}/marcar-entregue")
    @Operation(summary = "Marcar pedido como entregue")
    public ResponseEntity<PedidoDTO> marcarComoEntregue(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.marcarComoEntregue(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<PedidoDTO> cancelarPedido(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, motivo));
    }

    @GetMapping("/cozinha")
    @Operation(summary = "Listar pedidos para cozinha")
    public ResponseEntity<List<PedidoDTO>> findPedidosCozinha() {
        return ResponseEntity.ok(pedidoService.findPedidosCozinha());
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas de pedidos")
    public ResponseEntity<EstatisticasPedidosDTO> obterEstatisticas(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        return ResponseEntity.ok(pedidoService.obterEstatisticas(dataInicio, dataFim));
    }
}