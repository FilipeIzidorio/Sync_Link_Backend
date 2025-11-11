package com.synclink.application.controller;

import com.synclink.application.dto.CategoriaDTO;
import com.synclink.application.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Operações para gerenciamento de categorias (CRUD completo)")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // ============================================================
    // 🔹 LISTAR TODAS AS CATEGORIAS
    // ============================================================
    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CategoriaDTO>> findAll() {
        List<CategoriaDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    // ============================================================
    // 🔹 BUSCAR POR ID
    // ============================================================
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<CategoriaDTO> findById(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    // ============================================================
    // 🔹 CRIAR NOVA CATEGORIA
    // ============================================================
    @PostMapping
    @Operation(summary = "Criar nova categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CategoriaDTO> create(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO novaCategoria = categoriaService.create(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    // ============================================================
    // 🔹 ATUALIZAR CATEGORIA
    // ============================================================
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<CategoriaDTO> update(@PathVariable Long id,
                                               @Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO categoriaAtualizada = categoriaService.update(id, categoriaDTO);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    // ============================================================
    // 🔹 EXCLUIR CATEGORIA
    // ============================================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // 🔹 LISTAR CATEGORIAS ATIVAS
    // ============================================================
    @GetMapping("/ativas")
    @Operation(summary = "Listar categorias ativas")
    @ApiResponse(responseCode = "200", description = "Lista de categorias ativas retornada com sucesso")
    public ResponseEntity<List<CategoriaDTO>> findAtivas() {
        List<CategoriaDTO> categoriasAtivas = categoriaService.findByAtivo(true);
        return ResponseEntity.ok(categoriasAtivas);
    }
}
