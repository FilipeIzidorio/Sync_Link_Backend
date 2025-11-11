package com.synclink.application.controller;

import com.synclink.application.dto.MesaDTO;
import com.synclink.application.dto.MesaResumoDTO;
import com.synclink.application.service.MesaService;
import com.synclink.model.StatusMesa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Endpoints para gerenciamento das mesas do restaurante")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @Operation(summary = "Listar todas as mesas")
    public ResponseEntity<List<MesaDTO>> listarTodas() {
        return ResponseEntity.ok(mesaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mesa por ID")
    public ResponseEntity<MesaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova mesa")
    public ResponseEntity<MesaDTO> criar(@Valid @RequestBody MesaDTO dto) {
        MesaDTO nova = mesaService.create(dto);
        return ResponseEntity.created(URI.create("/api/mesas/" + nova.getId())).body(nova);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar informações de uma mesa existente")
    public ResponseEntity<MesaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody MesaDTO dto) {
        return ResponseEntity.ok(mesaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir mesa (somente se não houver pedido ativo)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da mesa (ex: LIVRE, OCUPADA, RESERVADA)")
    public ResponseEntity<MesaDTO> alterarStatus(@PathVariable Long id, @RequestParam StatusMesa status) {
        return ResponseEntity.ok(mesaService.updateStatus(id, status));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar mesas por status")
    public ResponseEntity<List<MesaDTO>> listarPorStatus(@PathVariable StatusMesa status) {
        return ResponseEntity.ok(mesaService.findByStatus(status));
    }

    @GetMapping("/livres")
    @Operation(summary = "Listar mesas livres")
    public ResponseEntity<List<MesaDTO>> listarLivres() {
        return ResponseEntity.ok(mesaService.findMesasLivres());
    }
    @GetMapping("/resumo")
    @Operation(summary = "Listar resumo das mesas (número e status) para o salão")
    public ResponseEntity<List<MesaResumoDTO>> listarResumo() {
        List<MesaResumoDTO> resumo = mesaService.findResumo();
        return ResponseEntity.ok(resumo);
    }
}
