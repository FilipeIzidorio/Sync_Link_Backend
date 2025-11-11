package com.synclink.application.service.impl;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.application.mapper.EstoqueMapper;
import com.synclink.application.service.EstoqueService;
import com.synclink.model.Estoque;
import com.synclink.domain.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
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
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado com ID: " + id));
        return estoqueMapper.toDto(estoque);
    }

    @Override
    public EstoqueDTO create(EstoqueDTO estoqueDTO) {
        Estoque estoque = estoqueMapper.toEntity(estoqueDTO);
        estoque = estoqueRepository.save(estoque);
        return estoqueMapper.toDto(estoque);
    }

    @Override
    public EstoqueDTO update(Long id, EstoqueDTO estoqueDTO) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado com ID: " + id));

        estoqueMapper.updateEntityFromDto(estoqueDTO, estoque);
        estoque = estoqueRepository.save(estoque);
        return estoqueMapper.toDto(estoque);
    }

    @Override
    public void delete(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado com ID: " + id));
        estoqueRepository.delete(estoque);
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
    public EstoqueDTO adicionarQuantidade(Long id, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado com ID: " + id));

        estoque.adicionarQuantidade(quantidade);
        estoque = estoqueRepository.save(estoque);
        return estoqueMapper.toDto(estoque);
    }

    @Override
    public EstoqueDTO removerQuantidade(Long id, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado com ID: " + id));

        estoque.removerQuantidade(quantidade);
        estoque = estoqueRepository.save(estoque);
        return estoqueMapper.toDto(estoque);
    }
}