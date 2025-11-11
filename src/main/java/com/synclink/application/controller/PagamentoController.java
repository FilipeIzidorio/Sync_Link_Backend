package com.synclink.application.controller;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.application.service.PagamentoService;
import com.synclink.model.FormaPagamento;
import com.synclink.model.StatusPagamento;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Operações para gerenciamento de pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Listar todos os pagamentos")
    public ResponseEntity<List<PagamentoDTO>> findAll() {
        return ResponseEntity.ok(pagamentoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Buscar pagamento por ID")
    public ResponseEntity<PagamentoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Criar novo pagamento")
    public ResponseEntity<PagamentoDTO> create(@Valid @RequestBody PagamentoDTO pagamentoDTO) {
        return ResponseEntity.ok(pagamentoService.create(pagamentoDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Atualizar pagamento")
    public ResponseEntity<PagamentoDTO> update(@PathVariable Long id, @Valid @RequestBody PagamentoDTO pagamentoDTO) {
        return ResponseEntity.ok(pagamentoService.update(id, pagamentoDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir pagamento")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pedido/{pedidoId}/processar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Processar pagamento para pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou pedido não fechado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<PagamentoDTO> processarPagamento(
            @PathVariable Long pedidoId,
            @RequestParam FormaPagamento formaPagamento,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(pedidoId, formaPagamento, valor));
    }

    @PostMapping("/pedido/{pedidoId}/processar-completo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Processar pagamento completo para pedido")
    public ResponseEntity<PagamentoDTO> processarPagamentoCompleto(
            @PathVariable Long pedidoId,
            @Valid @RequestBody PagamentoDTO pagamentoDTO) {
        return ResponseEntity.ok(pagamentoService.processarPagamentoCompleto(pedidoId, pagamentoDTO));
    }

    @PostMapping("/{id}/estornar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Estornar pagamento")
    public ResponseEntity<PagamentoDTO> estornarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.estornarPagamento(id));
    }

    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Confirmar pagamento pendente")
    public ResponseEntity<PagamentoDTO> confirmarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.confirmarPagamento(id));
    }

    @PostMapping("/{id}/recusar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Recusar pagamento pendente")
    public ResponseEntity<PagamentoDTO> recusarPagamento(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(pagamentoService.recusarPagamento(id, motivo));
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Listar pagamentos por pedido")
    public ResponseEntity<List<PagamentoDTO>> findByPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.findByPedidoId(pedidoId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Listar pagamentos por status")
    public ResponseEntity<List<PagamentoDTO>> findByStatus(@PathVariable StatusPagamento status) {
        return ResponseEntity.ok(pagamentoService.findByStatus(status));
    }

    @GetMapping("/forma-pagamento/{formaPagamento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar pagamentos por forma de pagamento")
    public ResponseEntity<List<PagamentoDTO>> findByFormaPagamento(@PathVariable FormaPagamento formaPagamento) {
        return ResponseEntity.ok(pagamentoService.findByFormaPagamento(formaPagamento));
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar pagamentos por período")
    public ResponseEntity<List<PagamentoDTO>> findPagamentosPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        return ResponseEntity.ok(pagamentoService.findPagamentosPorPeriodo(dataInicio, dataFim));
    }

    @GetMapping("/total-periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Calcular total de pagamentos por período")
    public ResponseEntity<BigDecimal> calcularTotalPagamentosPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        return ResponseEntity.ok(pagamentoService.calcularTotalPagamentosPeriodo(dataInicio, dataFim));
    }

    @GetMapping("/dia/{data}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Listar pagamentos do dia")
    public ResponseEntity<List<PagamentoDTO>> findPagamentosDia(@PathVariable String data) {
        return ResponseEntity.ok(pagamentoService.findPagamentosDia(data));
    }
}