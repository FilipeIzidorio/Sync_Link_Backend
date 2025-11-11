package com.synclink.application.controller;

import com.synclink.application.dto.MesaDTO;
import com.synclink.application.service.MesaService;
import com.synclink.model.StatusMesa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Operações para gerenciamento de mesas")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @Operation(summary = "Listar todas as mesas")
    public ResponseEntity<List<MesaDTO>> findAll() {
        return ResponseEntity.ok(mesaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mesa por ID")
    public ResponseEntity<MesaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova mesa")
    public ResponseEntity<MesaDTO> create(@Valid @RequestBody MesaDTO mesaDTO) {
        return ResponseEntity.ok(mesaService.create(mesaDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar mesa")
    public ResponseEntity<MesaDTO> update(@PathVariable Long id, @Valid @RequestBody MesaDTO mesaDTO) {
        return ResponseEntity.ok(mesaService.update(id, mesaDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir mesa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da mesa")
    public ResponseEntity<MesaDTO> updateStatus(@PathVariable Long id, @RequestParam StatusMesa status) {
        return ResponseEntity.ok(mesaService.updateStatus(id, status));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar mesas por status")
    public ResponseEntity<List<MesaDTO>> findByStatus(@PathVariable StatusMesa status) {
        return ResponseEntity.ok(mesaService.findByStatus(status));
    }

    @GetMapping("/livres")
    @Operation(summary = "Listar mesas livres")
    public ResponseEntity<List<MesaDTO>> findMesasLivres() {
        return ResponseEntity.ok(mesaService.findMesasLivres());
    }
}