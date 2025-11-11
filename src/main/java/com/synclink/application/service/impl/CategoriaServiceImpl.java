package com.synclink.application.service.impl;

import com.synclink.application.dto.CategoriaDTO;
import com.synclink.application.mapper.CategoriaMapper;
import com.synclink.application.service.CategoriaService;
import com.synclink.model.Categoria;
import com.synclink.domain.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        return categoriaMapper.toDtoList(categoriaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com ID: " + id));
        return categoriaMapper.toDto(categoria);
    }

    @Override
    public CategoriaDTO create(CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaMapper.toEntity(categoriaDTO);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(categoria);
    }

    @Override
    public CategoriaDTO update(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com ID: " + id));

        categoriaMapper.updateEntityFromDto(categoriaDTO, categoria);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(categoria);
    }

    @Override
    public void delete(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com ID: " + id));
        categoriaRepository.delete(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findByAtivo(Boolean ativo) {
        return categoriaMapper.toDtoList(categoriaRepository.findByAtivo(ativo));
    }
}