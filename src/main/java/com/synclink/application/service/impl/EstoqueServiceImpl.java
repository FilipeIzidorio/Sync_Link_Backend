package com.synclink.application.service.impl;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.application.mapper.EstoqueMapper;
import com.synclink.application.service.EstoqueService;
import com.synclink.domain.repository.EstoqueRepository;
import com.synclink.model.Estoque;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EstoqueDTO> findAll() {
        return estoqueMapper.toDtoList(estoqueRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public EstoqueDTO findById(Long id) {
        Estoque e = estoqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com ID: " + id));
        return estoqueMapper.toDto(e);
    }

    @Override
    public EstoqueDTO create(EstoqueDTO dto) {
        Estoque e = estoqueMapper.toEntity(dto);
        e.setDataEntrada(LocalDateTime.now());
        e = estoqueRepository.save(e);
        log.info("Novo estoque criado para o produto ID {}", e.getProduto().getId());
        return estoqueMapper.toDto(e);
    }

    @Override
    public EstoqueDTO update(Long id, EstoqueDTO dto) {
        Estoque e = estoqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com ID: " + id));
        estoqueMapper.updateEntityFromDto(dto, e);
        e = estoqueRepository.save(e);
        log.info("Estoque ID {} atualizado", id);
        return estoqueMapper.toDto(e);
    }

    @Override
    public void delete(Long id) {
        if (!estoqueRepository.existsById(id))
            throw new EntityNotFoundException("Estoque não encontrado com ID: " + id);
        estoqueRepository.deleteById(id);
        log.info("Estoque ID {} excluído com sucesso", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstoqueDTO> findByProdutoId(Long produtoId) {
        return estoqueMapper.toDtoList(estoqueRepository.findByProdutoId(produtoId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstoqueDTO> findPrecisaRepor() {
        return estoqueMapper.toDtoList(estoqueRepository.findByQuantidadeLessThanEqualEstoqueMinimo());
    }

    @Override
    public EstoqueDTO adicionarQuantidade(Long id, Integer qtd) {
        Estoque e = estoqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com ID: " + id));
        e.adicionarQuantidade(qtd);
        e = estoqueRepository.save(e);
        log.info("{} unidades adicionadas ao estoque ID {}", qtd, id);
        return estoqueMapper.toDto(e);
    }

    @Override
    public EstoqueDTO removerQuantidade(Long id, Integer qtd) {
        Estoque e = estoqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com ID: " + id));
        e.removerQuantidade(qtd);
        e = estoqueRepository.save(e);
        log.info("{} unidades removidas do estoque ID {}", qtd, id);
        return estoqueMapper.toDto(e);
    }
}
