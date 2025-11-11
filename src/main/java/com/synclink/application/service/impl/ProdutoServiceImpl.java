package com.synclink.application.service.impl;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.application.mapper.ProdutoMapper;
import com.synclink.application.service.ProdutoService;
import com.synclink.model.Produto;
import com.synclink.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
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
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO create(ProdutoDTO produtoDTO) {
        Produto produto = produtoMapper.toEntity(produtoDTO);
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO update(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));

        produtoMapper.updateEntityFromDto(produtoDTO, produto);
        produto.setDataAtualizacao(java.time.LocalDateTime.now());
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public void delete(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));
        produtoRepository.delete(produto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findByCategoriaId(Long categoriaId) {
        return produtoMapper.toDtoList(produtoRepository.findByCategoriaId(categoriaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findByAtivo(Boolean ativo) {
        return produtoMapper.toDtoList(produtoRepository.findByAtivo(ativo));
    }

    @Override
    public ProdutoDTO ativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));

        produto.setAtivo(true);
        produto.setDataAtualizacao(java.time.LocalDateTime.now());
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO inativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + id));

        produto.setAtivo(false);
        produto.setDataAtualizacao(java.time.LocalDateTime.now());
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }
}