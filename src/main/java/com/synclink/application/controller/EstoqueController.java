package com.synclink.application.controller;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Operações para gerenciamento de estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @Operation(summary = "Listar todo o estoque")
    public ResponseEntity<List<EstoqueDTO>> findAll() {
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estoque por ID")
    public ResponseEntity<EstoqueDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo registro de estoque")
    public ResponseEntity<EstoqueDTO> create(@Valid @RequestBody EstoqueDTO estoqueDTO) {
        return ResponseEntity.ok(estoqueService.create(estoqueDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estoque")
    public ResponseEntity<EstoqueDTO> update(@PathVariable Long id, @Valid @RequestBody EstoqueDTO estoqueDTO) {
        return ResponseEntity.ok(estoqueService.update(id, estoqueDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de estoque")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Buscar estoque por produto")
    public ResponseEntity<List<EstoqueDTO>> findByProdutoId(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.findByProdutoId(produtoId));
    }

    @GetMapping("/reposicao")
    @Operation(summary = "Listar itens que precisam de reposição")
    public ResponseEntity<List<EstoqueDTO>> findPrecisaRepor() {
        return ResponseEntity.ok(estoqueService.findPrecisaRepor());
    }

    @PatchMapping("/{id}/adicionar")
    @Operation(summary = "Adicionar quantidade ao estoque")
    public ResponseEntity<EstoqueDTO> adicionarQuantidade(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(estoqueService.adicionarQuantidade(id, quantidade));
    }

    @PatchMapping("/{id}/remover")
    @Operation(summary = "Remover quantidade do estoque")
    public ResponseEntity<EstoqueDTO> removerQuantidade(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(estoqueService.removerQuantidade(id, quantidade));
    }
}