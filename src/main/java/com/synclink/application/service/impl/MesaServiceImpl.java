package com.synclink.application.service.impl;

import com.synclink.application.dto.MesaDTO;
import com.synclink.application.dto.MesaResumoDTO;
import com.synclink.application.mapper.MesaMapper;
import com.synclink.application.service.MesaService;
import com.synclink.domain.repository.MesaRepository;
import com.synclink.model.Mesa;
import com.synclink.model.enums.StatusMesa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MesaDTO> findAll() {
        return mesaMapper.toDtoList(mesaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public MesaDTO findById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada com ID: " + id));
        return mesaMapper.toDto(mesa);
    }

    @Override
    public MesaDTO create(MesaDTO dto) {
        if (mesaRepository.existsByNumero(dto.getNumero())) {
            throw new IllegalArgumentException("Já existe uma mesa com o número: " + dto.getNumero());
        }

        Mesa mesa = mesaMapper.toEntity(dto);
        mesa.setStatus(StatusMesa.LIVRE);
        Mesa saved = mesaRepository.save(mesa);

        log.info("✅ Mesa número {} criada com sucesso (ID: {})", saved.getNumero(), saved.getId());
        return mesaMapper.toDto(saved);
    }

    @Override
    public MesaDTO update(Long id, MesaDTO dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada com ID: " + id));

        if (!mesa.getNumero().equals(dto.getNumero()) && mesaRepository.existsByNumero(dto.getNumero())) {
            throw new IllegalArgumentException("Já existe uma mesa com o número: " + dto.getNumero());
        }

        mesaMapper.updateEntityFromDto(dto, mesa);
        Mesa updated = mesaRepository.save(mesa);

        log.info("🔁 Mesa {} atualizada com sucesso", id);
        return mesaMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada com ID: " + id));

        if (mesa.temPedidoAtivo()) {
            throw new IllegalStateException("Não é possível excluir uma mesa com pedido ativo");
        }

        mesaRepository.delete(mesa);
        log.info("🗑️ Mesa {} removida com sucesso", id);
    }

    @Override
    public MesaDTO updateStatus(Long id, StatusMesa status) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada com ID: " + id));

        mesa.setStatus(status);
        mesaRepository.save(mesa);

        log.info("🚦 Status da mesa {} alterado para {}", mesa.getNumero(), status);
        return mesaMapper.toDto(mesa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaDTO> findByStatus(StatusMesa status) {
        return mesaMapper.toDtoList(mesaRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaDTO> findMesasLivres() {
        return mesaMapper.toDtoList(mesaRepository.findByStatus(StatusMesa.LIVRE));
    }
    // implementação
    @Override
    @Transactional(readOnly = true)
    public List<MesaResumoDTO> findResumo() {
        return mesaRepository.findAll().stream()
                .map(mesa -> MesaResumoDTO.builder()
                        .numero(mesa.getNumero())
                        .status(mesa.getStatus())
                        .build())
                .toList();
    }

}
