package com.synclink.application.service.impl;

import com.synclink.application.dto.MesaDTO;
import com.synclink.application.mapper.MesaMapper;
import com.synclink.application.service.MesaService;
import com.synclink.model.Mesa;
import com.synclink.model.StatusMesa;
import com.synclink.domain.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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
                .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + id));
        return mesaMapper.toDto(mesa);
    }

    @Override
    public MesaDTO create(MesaDTO mesaDTO) {
        // Verificar se número da mesa já existe
        if (mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new IllegalArgumentException("Já existe uma mesa com o número: " + mesaDTO.getNumero());
        }

        Mesa mesa = mesaMapper.toEntity(mesaDTO);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDto(mesa);
    }

    @Override
    public MesaDTO update(Long id, MesaDTO mesaDTO) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + id));

        // Verificar se novo número já existe (se foi alterado)
        if (!mesa.getNumero().equals(mesaDTO.getNumero()) &&
                mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new IllegalArgumentException("Já existe uma mesa com o número: " + mesaDTO.getNumero());
        }

        mesaMapper.updateEntityFromDto(mesaDTO, mesa);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDto(mesa);
    }

    @Override
    public void delete(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + id));

        // Verificar se mesa tem pedidos ativos
        if (mesa.temPedidoAtivo()) {
            throw new IllegalStateException("Não é possível excluir mesa com pedido ativo");
        }

        mesaRepository.delete(mesa);
    }

    @Override
    public MesaDTO updateStatus(Long id, StatusMesa status) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + id));

        mesa.setStatus(status);
        mesa = mesaRepository.save(mesa);
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
}