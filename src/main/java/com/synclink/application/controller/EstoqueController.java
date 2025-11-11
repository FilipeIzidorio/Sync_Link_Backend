package com.synclink.application.controller;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estoques")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Gerenciamento de registros de estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @Operation(summary = "Listar todos os registros de estoque")
    public ResponseEntity<List<EstoqueDTO>> findAll() {
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um registro de estoque pelo ID")
    public ResponseEntity<EstoqueDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo registro de estoque")
    public ResponseEntity<EstoqueDTO> create(@Valid @RequestBody EstoqueDTO dto) {
        EstoqueDTO created = estoqueService.create(dto);
        return ResponseEntity.created(URI.create("/api/estoques/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um registro existente de estoque")
    public ResponseEntity<EstoqueDTO> update(@PathVariable Long id, @Valid @RequestBody EstoqueDTO dto) {
        return ResponseEntity.ok(estoqueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um registro de estoque")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar estoque de um produto específico")
    public ResponseEntity<List<EstoqueDTO>> findByProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.findByProdutoId(produtoId));
    }

    @GetMapping("/reposicao")
    @Operation(summary = "Listar itens que precisam de reposição")
    public ResponseEntity<List<EstoqueDTO>> findPrecisaRepor() {
        return ResponseEntity.ok(estoqueService.findPrecisaRepor());
    }

    @PatchMapping("/{id}/adicionar")
    @Operation(summary = "Adicionar quantidade ao estoque")
    public ResponseEntity<EstoqueDTO> adicionar(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(estoqueService.adicionarQuantidade(id, quantidade));
    }

    @PatchMapping("/{id}/remover")
    @Operation(summary = "Remover quantidade do estoque")
    public ResponseEntity<EstoqueDTO> remover(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(estoqueService.removerQuantidade(id, quantidade));
    }
}
