package com.synclink.application.controller;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.application.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Operações de gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos")
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        log.info("Listando todos os produtos");
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoDTO> findById(@PathVariable Long id) {
        log.info("Buscando produto com ID {}", id);
        return ResponseEntity.ok(produtoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<ProdutoDTO> create(@Valid @RequestBody ProdutoDTO produtoDTO) {
        log.info("Criando novo produto: {}", produtoDTO.getNome());
        ProdutoDTO created = produtoService.create(produtoDTO);
        return ResponseEntity
                .created(URI.create("/api/produtos/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto existente")
    public ResponseEntity<ProdutoDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        log.info("Atualizando produto ID {}", id);
        return ResponseEntity.ok(produtoService.update(id, produtoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Excluindo produto ID {}", id);
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar produtos por categoria")
    public ResponseEntity<List<ProdutoDTO>> findByCategoriaId(@PathVariable Long categoriaId) {
        log.info("Listando produtos da categoria {}", categoriaId);
        return ResponseEntity.ok(produtoService.findByCategoriaId(categoriaId));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar produtos ativos")
    public ResponseEntity<List<ProdutoDTO>> findAtivos() {
        log.info("Listando produtos ativos");
        return ResponseEntity.ok(produtoService.findByAtivo(true));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar produto")
    public ResponseEntity<ProdutoDTO> ativar(@PathVariable Long id) {
        log.info("Ativando produto ID {}", id);
        return ResponseEntity.ok(produtoService.ativar(id));
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar produto")
    public ResponseEntity<ProdutoDTO> inativar(@PathVariable Long id) {
        log.info("Inativando produto ID {}", id);
        return ResponseEntity.ok(produtoService.inativar(id));
    }
}
