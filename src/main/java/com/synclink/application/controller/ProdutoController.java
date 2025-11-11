package com.synclink.application.controller;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.application.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Operações para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos")
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<ProdutoDTO> create(@Valid @RequestBody ProdutoDTO produtoDTO) {
        return ResponseEntity.ok(produtoService.create(produtoDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    public ResponseEntity<ProdutoDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        return ResponseEntity.ok(produtoService.update(id, produtoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar produtos por categoria")
    public ResponseEntity<List<ProdutoDTO>> findByCategoriaId(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.findByCategoriaId(categoriaId));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar produtos ativos")
    public ResponseEntity<List<ProdutoDTO>> findAtivos() {
        return ResponseEntity.ok(produtoService.findByAtivo(true));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar produto")
    public ResponseEntity<ProdutoDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.ativar(id));
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar produto")
    public ResponseEntity<ProdutoDTO> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.inativar(id));
    }
}