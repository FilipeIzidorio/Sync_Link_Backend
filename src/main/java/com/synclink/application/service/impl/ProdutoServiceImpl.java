package com.synclink.application.service.impl;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.application.mapper.ProdutoMapper;
import com.synclink.application.service.ProdutoService;
import com.synclink.domain.repository.ProdutoRepository;
import com.synclink.model.Produto;
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
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findAll() {
        return produtoMapper.toDtoList(produtoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO create(ProdutoDTO produtoDTO) {
        if (produtoRepository.existsByNomeAndCategoriaId(produtoDTO.getNome(), produtoDTO.getCategoriaId())) {
            throw new IllegalArgumentException("Já existe um produto com este nome nesta categoria");
        }
        Produto produto = produtoMapper.toEntity(produtoDTO);
        produto.setDataCriacao(LocalDateTime.now());
        produto.setAtivo(true);
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO update(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produtoMapper.updateEntityFromDto(produtoDTO, produto);
        produto.setDataAtualizacao(LocalDateTime.now());
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public void delete(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
        log.info("Produto ID {} excluído com sucesso", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findByCategoriaId(Long categoriaId) {
        return produtoMapper.toDtoList(produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findByAtivo(Boolean ativo) {
        return produtoMapper.toDtoList(produtoRepository.findByAtivo(ativo));
    }

    @Override
    public ProdutoDTO ativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));
        produto.setAtivo(true);
        produto.setDataAtualizacao(LocalDateTime.now());
        return produtoMapper.toDto(produtoRepository.save(produto));
    }

    @Override
    public ProdutoDTO inativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));
        produto.setAtivo(false);
        produto.setDataAtualizacao(LocalDateTime.now());
        return produtoMapper.toDto(produtoRepository.save(produto));
    }
}
