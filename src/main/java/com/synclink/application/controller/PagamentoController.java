package com.synclink.application.controller;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.application.service.PagamentoService;
import com.synclink.model.enums.FormaPagamento;
import com.synclink.model.enums.StatusPagamento;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Pagamentos", description = "Endpoints de controle e processamento de pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    @Operation(summary = "Listar todos os pagamentos")
    public ResponseEntity<List<PagamentoDTO>> listarTodos() {
        return ResponseEntity.ok(pagamentoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID")
    public ResponseEntity<PagamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar novo pagamento")
    public ResponseEntity<PagamentoDTO> criar(@Valid @RequestBody PagamentoDTO dto) {
        return ResponseEntity.ok(pagamentoService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar informações de pagamento")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PagamentoDTO dto) {
        return ResponseEntity.ok(pagamentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir pagamento definitivamente")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pagamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pedido/{pedidoId}/processar")
    @Operation(summary = "Processar pagamento para pedido")
    public ResponseEntity<PagamentoDTO> processarPagamento(
            @PathVariable Long pedidoId,
            @RequestParam FormaPagamento formaPagamento,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(pedidoId, formaPagamento, valor));
    }

    @PostMapping("/pedido/{pedidoId}/completo")
    @Operation(summary = "Processar pagamento completo para pedido")
    public ResponseEntity<PagamentoDTO> processarPagamentoCompleto(
            @PathVariable Long pedidoId,
            @Valid @RequestBody PagamentoDTO dto) {
        return ResponseEntity.ok(pagamentoService.processarPagamentoCompleto(pedidoId, dto));
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar pagamento pendente")
    public ResponseEntity<PagamentoDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.confirmarPagamento(id));
    }

    @PostMapping("/{id}/estornar")
    @Operation(summary = "Estornar pagamento")
    public ResponseEntity<PagamentoDTO> estornar(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.estornarPagamento(id));
    }

    @PostMapping("/{id}/recusar")
    @Operation(summary = "Recusar pagamento pendente")
    public ResponseEntity<PagamentoDTO> recusar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(pagamentoService.recusarPagamento(id, motivo));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Listar pagamentos de um pedido")
    public ResponseEntity<List<PagamentoDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.findByPedidoId(pedidoId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pagamentos por status")
    public ResponseEntity<List<PagamentoDTO>> listarPorStatus(@PathVariable StatusPagamento status) {
        return ResponseEntity.ok(pagamentoService.findByStatus(status));
    }

    @GetMapping("/forma/{formaPagamento}")
    @Operation(summary = "Listar pagamentos por forma de pagamento")
    public ResponseEntity<List<PagamentoDTO>> listarPorFormaPagamento(@PathVariable FormaPagamento formaPagamento) {
        return ResponseEntity.ok(pagamentoService.findByFormaPagamento(formaPagamento));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar pagamentos por período")
    public ResponseEntity<List<PagamentoDTO>> listarPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        return ResponseEntity.ok(pagamentoService.findPagamentosPorPeriodo(dataInicio, dataFim));
    }

    @GetMapping("/total-periodo")
    @Operation(summary = "Obter total de pagamentos aprovados por período")
    public ResponseEntity<BigDecimal> totalPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        return ResponseEntity.ok(pagamentoService.calcularTotalPagamentosPeriodo(dataInicio, dataFim));
    }

    @GetMapping("/dia/{data}")
    @Operation(summary = "Listar pagamentos de um dia específico")
    public ResponseEntity<List<PagamentoDTO>> listarPorDia(@PathVariable String data) {
        return ResponseEntity.ok(pagamentoService.findPagamentosDia(data));
    }
}
