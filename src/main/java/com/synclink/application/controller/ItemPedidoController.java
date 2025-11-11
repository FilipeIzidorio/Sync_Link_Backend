package com.synclink.application.controller;

import com.synclink.application.dto.ItemPedidoDTO;
import com.synclink.application.service.ItemPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
@RequiredArgsConstructor
@Tag(name = "Itens do Pedido", description = "Operações de gerenciamento de itens dentro dos pedidos")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    // ==============================
    // CRUD BÁSICO
    // ==============================
    @GetMapping
    @Operation(summary = "Listar todos os itens de pedido")
    public ResponseEntity<List<ItemPedidoDTO>> findAll() {
        return ResponseEntity.ok(itemPedidoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de pedido por ID")
    public ResponseEntity<ItemPedidoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemPedidoService.findById(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Listar todos os itens pertencentes a um pedido específico")
    public ResponseEntity<List<ItemPedidoDTO>> findByPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(itemPedidoService.findByPedidoId(pedidoId));
    }

    @PostMapping
    @Operation(summary = "Adicionar novo item a um pedido")
    public ResponseEntity<ItemPedidoDTO> create(@Valid @RequestBody ItemPedidoDTO dto) {
        ItemPedidoDTO created = itemPedidoService.create(dto);
        URI uri = URI.create("/api/itens-pedido/" + created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de pedido existente")
    public ResponseEntity<ItemPedidoDTO> update(@PathVariable Long id,
                                                @Valid @RequestBody ItemPedidoDTO dto) {
        return ResponseEntity.ok(itemPedidoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item de pedido")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemPedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
